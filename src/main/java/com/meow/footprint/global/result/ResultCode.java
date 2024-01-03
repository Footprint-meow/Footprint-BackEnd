package com.meow.footprint.global.result;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {
    // Member
    REGISTER_SUCCESS(201,  "회원가입에 성공하였습니다."),
    MEMBER_ID_EXIST(200,  "회원 id가 이미 존재합니다."),
    MEMBER_ID_NOT_EXIST(200,  "회원 id가 존재하지 않습니다."),
    MEMBER_FIND_SUCCESS(200,  "회원정보 조회에 성공하였습니다."),
    MEMBER_UPDATE_SUCCESS(200,  "회원정보 수정에 성공하였습니다."),
    MEMBER_DELETE_SUCCESS(200,  "회원 탈퇴에 성공하였습니다."),
    LOGIN_SUCCESS(200,  "로그인에 성공하였습니다."),
    LOGOUT_SUCCESS(200,  "로그아웃에 성공하였습니다."),
    PASSWORD_UPDATE_SUCCESS(200,  "비밀번호 변경에 성공하였습니다."),

    // Guestbook
    CREATE_GUESTBOOK_SUCCESS(201,"방명록 생성에 성공하였습니다."),
    GET_GUESTBOOK_LIST_SUCCESS(200,"방명록 목록 조회에 성공하였습니다."),
    DELETE_GUESTBOOK_SUCCESS(200,"방명록 삭제에 성공하였습니다."),
    UPDATE_GUESTBOOK_SUCCESS(200,"방명록 수정에 성공하였습니다."),
    GET_GUESTBOOK_SIMPLE_SUCCESS(200,"방명록 개별 조회에 성공하였습니다."),

    // footprint
    CREATE_FOOTPRINT_SUCCESS(201,"발자국 생성에 성공하였습니다."),
    GET_SECRET_FOOTPRINT_SUCCESS(200,"발자국 비밀글 조회에 성공하였습니다."),
    GET_FOOTPRINT_LIST_SUCCESS(200,"발자국 목록 조회에 성공하였습니다.");


    private final int status;
    private final String message;
}
