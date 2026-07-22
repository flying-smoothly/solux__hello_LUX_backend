package com.solux.web.global.quiz;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

/**
 * 퀴즈 통계 포트의 기본 구현 설정.
 * 퀴즈 팀이 {@link QuizStatsPort} 구현 빈을 등록하면
 * {@link ConditionalOnMissingBean} 조건에 의해 이 기본 구현은 사용되지 않는다.
 */
@Configuration
public class QuizStatsConfig {

    @Bean
    @ConditionalOnMissingBean(QuizStatsPort.class)
    public QuizStatsPort defaultQuizStatsPort() {
        return new QuizStatsPort() {
            @Override
            public Integer getLastScore(Integer pCode) {
                return null;
            }

            @Override
            public Integer getAverageScore(Integer pCode) {
                return null;
            }

            @Override
            public String getTrend(Integer pCode) {
                return "데이터 없음";
            }

            @Override
            public LocalDate getLastQuizDate(Integer pCode) {
                return null;
            }

            @Override
            public List<String> getTrendLabels(Integer pCode, String period) {
                return Collections.emptyList();
            }

            @Override
            public List<Integer> getTrendScores(Integer pCode, String period) {
                return Collections.emptyList();
            }
        };
    }
}
