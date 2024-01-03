package com.meow.footprint.domain.footprint.service;

import com.meow.footprint.domain.footprint.dto.*;
import com.meow.footprint.domain.footprint.entity.Footprint;
import com.meow.footprint.domain.footprint.repository.FootprintRepository;
import com.meow.footprint.domain.guestbook.entity.Guestbook;
import com.meow.footprint.domain.guestbook.repository.GuestbookRepository;
import com.meow.footprint.global.result.error.exception.BusinessException;
import com.meow.footprint.global.util.AccountUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static com.meow.footprint.global.result.error.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class FootprintServiceImpl implements FootprintService{
    private final FootprintRepository footprintRepository;
    private final GuestbookRepository guestbookRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final AccountUtil accountUtil;
    private static final int DEGREE = 100; //발자국 작성 가능 범위

    @Transactional
    @Override
    public void createFootprint(FootprintRequest footprintRequest) {
        Footprint footprint = modelMapper.map(footprintRequest,Footprint.class);
        Guestbook guestbook = guestbookRepository
                .findById(footprintRequest.guestbook())
                .orElseThrow(()-> new BusinessException(GUESTBOOK_ID_NOT_EXIST));
        if(!checkLocation(guestbook,footprintRequest)){
            throw new BusinessException(OUT_OF_AREA);
        }
        footprint.encodingPassword(passwordEncoder);
        guestbook.setUpdate(true);
        footprint.setGuestbook(guestbook);
        footprintRepository.save(footprint);
    }

    @Transactional
    @Override
    public FootprintResponse getSecretFootprint(long footprintId, FootprintPassword footprintPassword) {
        Footprint footprint = checkFootprintAuthority(footprintId, footprintPassword);
        footprint.setChecked(true);
        return FootprintResponse.from(footprint);
    }

    @Override
    public FootprintByDateSliceDTO getFootprintListByDate(String guestbookId, int page, int size) {
        Pageable pageable = PageRequest.of(page,size);
        Slice<FootprintResponse> responseSlice = footprintRepository.getFootprintListByDate(guestbookId,pageable);

        List<FootprintByDateDTO> footprintByDateDTOList = responseSlice.stream()
                .collect(Collectors.groupingBy(FootprintResponse::getCreateDate))
                .entrySet().stream()
                .map(entry -> new FootprintByDateDTO(entry.getKey(),entry.getValue()))
                .collect(Collectors.toList());

        return new FootprintByDateSliceDTO(footprintByDateDTOList
                ,responseSlice.getNumber()
                ,responseSlice.getSize()
                ,responseSlice.isFirst()
                ,responseSlice.isLast());
    }

    @Transactional
    @Override
    public void deleteFootprint(long footprintId, FootprintPassword footprintPassword) {
        Footprint footprint = checkFootprintAuthority(footprintId, footprintPassword);
        footprintRepository.delete(footprint);
    }

    @Transactional
    @Override
    public void readCheckFootprint(long footprintId) {
        Footprint footprint = footprintRepository.findById(footprintId).orElseThrow(()->new BusinessException(FOOTPRINT_ID_NOT_EXIST));
        try {
            String loginMemberId = accountUtil.getLoginMemberId();
            if(footprint.getGuestbook().getHost().getId().equals(loginMemberId)){
                footprint.setChecked(true);
                footprintRepository.save(footprint);
            }
        }catch (RuntimeException e){
            log.info("토큰없음");
        }
    }

    private Footprint checkFootprintAuthority(long footprintId, FootprintPassword footprintPassword) {
        Footprint footprint = footprintRepository.findById(footprintId).orElseThrow(()-> new BusinessException(FOOTPRINT_ID_NOT_EXIST));
        String loginId = null;
        try {
            loginId = accountUtil.getLoginMemberId();
        }catch (RuntimeException e){
            log.info("토큰없음");
        }
        if(!passwordEncoder.matches(footprintPassword.password(), footprint.getPassword())
                && !footprint.getGuestbook().getHost().getId().equals(loginId)) {
            throw new BusinessException(FORBIDDEN_ERROR);
        }
        return footprint;
    }

    //좌표(위도,경도)를 이용한 거리계산
    public boolean checkLocation(Guestbook guestbook,FootprintRequest footprintRequest){
        double latBook = guestbook.getLatitude();
        double lonBook = guestbook.getLongitude();

        double latFoot = footprintRequest.latitude();
        double lonFoot = footprintRequest.longitude();

        double theta = lonBook - lonFoot;
        double dist = Math.sin(Math.toRadians(latBook))
                * Math.sin(Math.toRadians(latFoot))
                + Math.cos(Math.toRadians(latBook))
                * Math.cos(Math.toRadians(latFoot))
                * Math.cos(Math.toRadians(theta));
        dist = Math.acos(dist);
        dist = Math.toDegrees(dist);
        dist *= 60*1.1515*1609.344;  //meter단위 거리
        return dist <= DEGREE;
    }
}
