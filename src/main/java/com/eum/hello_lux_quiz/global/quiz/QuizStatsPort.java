package com.eum.hello_lux_quiz.global.quiz;

import java.time.LocalDate;
import java.util.List;

/**
 * 퀴즈 결과 집계 조회용 포트(인터페이스).
 *
 * <p>대시보드/추이/리포트 등 보호자·의사 기능은 퀴즈 결과 집계에 의존한다.
 * 퀴즈 도메인은 다른 팀원이 구현하므로, 이 인터페이스를 구현한 스프링 빈을 등록하면
 * 아래 기능들이 실제 데이터를 사용하게 된다.</p>
 *
 * <p>구현 빈이 없을 경우 {@code QuizStatsConfig} 의 기본(no-op) 구현이 사용되어
 * 애플리케이션이 정상 기동하며, 빈 값/기본값을 반환한다.</p>
 */
public interface QuizStatsPort {

    /** 최근 퀴즈 점수. 없으면 null. */
    Integer getLastScore(Integer pCode);

    /** 평균 점수. 없으면 null. */
    Integer getAverageScore(Integer pCode);

    /** 추세 문자열(예: "상승", "하락", "유지"). 데이터가 없으면 "데이터 없음". */
    String getTrend(Integer pCode);

    /** 마지막 퀴즈 응시 날짜. 없으면 null. */
    LocalDate getLastQuizDate(Integer pCode);

    /** 추이 그래프의 x축 라벨(예: ["5/1","5/8"]). period 는 week/month 등. */
    List<String> getTrendLabels(Integer pCode, String period);

    /** 추이 그래프의 점수 값(labels 와 동일 길이). */
    List<Integer> getTrendScores(Integer pCode, String period);
}
