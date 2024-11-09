package dbdr.openai.service;

import dbdr.openai.dto.response.SummaryAndTagResponse;
import dbdr.openai.dto.response.SummaryResponse;
import dbdr.openai.dto.response.TagResponse;
import dbdr.openai.entity.Summary;
import dbdr.openai.repository.SummaryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SummaryService {

    private final SummaryRepository summaryRepository;

    public SummaryAndTagResponse getSummaryAndTag(Long chartId) {
        return new SummaryAndTagResponse(getSummarization(chartId), getTag(chartId));
    }

    private SummaryResponse getSummarization(Long chartId) {
        Summary summary = summaryRepository.findByChartId(chartId);
        return new SummaryResponse(summary.getCognitiveManagement(), summary.getBodyManagement(),
            summary.getRecoveryTraining(), summary.getConditionDisease(),
            summary.getNursingManagement());
    }

    private TagResponse getTag(Long chartId) {
        Summary summary = summaryRepository.findByChartId(chartId);
        return new TagResponse(summary.getTagOne(), summary.getTagTwo(), summary.getTagThree());
    }
}
