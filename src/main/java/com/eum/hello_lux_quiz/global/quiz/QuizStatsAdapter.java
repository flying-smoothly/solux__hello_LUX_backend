package com.eum.hello_lux_quiz.global.quiz;

import com.eum.hello_lux_quiz.domain.QuizResult;
import com.eum.hello_lux_quiz.repository.QuizResultRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;

/**
 * quiz_llm 도메인(퀴즈 결과)을 authentication 도메인의 {@link QuizStatsPort} 로 연결하는 어댑터.
 *
 * <p>
 * 이 빈이 등록되면 {@code QuizStatsConfig} 의 {@code @ConditionalOnMissingBean}
 * 기본(no-op) 구현은 사용되지 않고, 보호자/의사 대시보드·추이·리포트가 실제 퀴즈 결과 데이터를 사용한다.</p>
 *
 * <p>
 * 점수는 정답 수 기반 백분율(correctCount * 100 / totalCount)로 계산한다.</p>
 */
@Component
@RequiredArgsConstructor
public class QuizStatsAdapter implements QuizStatsPort {

    private static final DateTimeFormatter LABEL_FORMAT = DateTimeFormatter.ofPattern("M/d");

    private final QuizResultRepository quizResultRepository;

    private static int toScore(QuizResult r) {
        if (r.getTotalCount() <= 0) {
            return 0;
        }
        return Math.round(r.getCorrectCount() * 100f / r.getTotalCount());
    }

    /**
     * 날짜 오름차순 정렬된 해당 환자의 결과 목록.
     */
    private List<QuizResult> orderedResults(Integer pCode) {
        return quizResultRepository.findByPCode(pCode).stream()
                .sorted(Comparator.comparing(QuizResult::getDate))
                .toList();
    }

    private LocalDate periodStart(String period) {
        LocalDate today = LocalDate.now();
        if (period == null) {
            return today.minusWeeks(1);
        }
        return switch (period) {
            case "month" ->
                today.minusMonths(1);
            case "year" ->
                today.minusYears(1);
            default ->
                today.minusWeeks(1); // week 및 기타
        };
    }

    private List<QuizResult> resultsInPeriod(Integer pCode, String period) {
        return quizResultRepository
                .findByPCodeAndDateBetween(pCode, periodStart(period), LocalDate.now())
                .stream()
                .sorted(Comparator.comparing(QuizResult::getDate))
                .toList();
    }

    @Override
    public Integer getLastScore(Integer pCode) {
        List<QuizResult> results = orderedResults(pCode);
        if (results.isEmpty()) {
            return null;
        }
        return toScore(results.get(results.size() - 1));
    }

    @Override
    public Integer getAverageScore(Integer pCode) {
        List<QuizResult> results = orderedResults(pCode);
        if (results.isEmpty()) {
            return null;
        }
        double avg = results.stream().mapToInt(QuizStatsAdapter::toScore).average().orElse(0);
        return (int) Math.round(avg);
    }

    @Override
    public String getTrend(Integer pCode) {
        List<QuizResult> results = orderedResults(pCode);
        if (results.size() < 2) {
            return "데이터 없음";
        }
        int prev = toScore(results.get(results.size() - 2));
        int last = toScore(results.get(results.size() - 1));
        if (last > prev) {
            return "상승";
        }
        if (last < prev) {
            return "하락";
        }
        return "유지";
    }

    @Override
    public LocalDate getLastQuizDate(Integer pCode) {
        List<QuizResult> results = orderedResults(pCode);
        if (results.isEmpty()) {
            return null;
        }
        return results.get(results.size() - 1).getDate();
    }

    @Override
    public List<String> getTrendLabels(Integer pCode, String period) {
        return resultsInPeriod(pCode, period).stream()
                .map(r -> r.getDate().format(LABEL_FORMAT))
                .toList();
    }

    @Override
    public List<Integer> getTrendScores(Integer pCode, String period) {
        return resultsInPeriod(pCode, period).stream()
                .map(QuizStatsAdapter::toScore)
                .toList();
    }
}
