package com.example.librarymanagement.services;

import com.example.librarymanagement.dtos.models.EvaluateModel;
import com.example.librarymanagement.dtos.requests.action.EvaluateBookRequest;
import com.example.librarymanagement.dtos.responses.rating.RatingCountResponse;
import com.example.librarymanagement.dtos.responses.rating.RatingSummaryResponse;
import com.example.librarymanagement.entities.Evaluate;
import com.example.librarymanagement.entities.Record;
import com.example.librarymanagement.entities.User;
import com.example.librarymanagement.enums.ErrorCode;
import com.example.librarymanagement.exception.AppException;
import com.example.librarymanagement.repositories.BookRepository;
import com.example.librarymanagement.repositories.EvaluateRepository;
import com.example.librarymanagement.repositories.RecordRepository;
import com.example.librarymanagement.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class EvaluateService {

    @Autowired
    private EvaluateRepository evaluateRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private RecordRepository recordRepository;

    public EvaluateModel toModel(Evaluate evaluate) {
        return new EvaluateModel(
                evaluate.getUser().getFullName(),
                evaluate.getTitle(),
                evaluate.getRating(),
                evaluate.getComment(),
                evaluate.getEvaluateDay(),
                true
        );
    }

    @PreAuthorize("hasRole('USER')")
    public Map<String, Boolean> checkEvaluated(String title) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("sub");
            User userCurrent = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
            boolean evaluated = evaluateRepository.existsByUserAndTitle(userCurrent, title);
            return Map.of("evaluated", evaluated);
        }
        throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    public List<EvaluateModel> seeEvaluated(String title) {
        List<Evaluate> evaluates = evaluateRepository.findByTitle(title);
        return evaluates.stream()
                .map(this::toModel)
                .collect(Collectors.toList());
    }

    @PreAuthorize("hasRole('USER')")
    public EvaluateModel evaluateBook(EvaluateBookRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication.getPrincipal() instanceof Jwt jwt) {
            String email = jwt.getClaimAsString("sub");
            User userCurrent = userRepository.findByEmail(email)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

            boolean bookExists = bookRepository.existsByTitle(request.getTitle());
            if(!bookExists) throw new AppException(ErrorCode.BOOK_NOT_FOUND);

            boolean evaluated = evaluateRepository.existsByUserAndTitle(userCurrent, request.getTitle());
            if(evaluated) throw new AppException(ErrorCode.BOOK_EVALUATED);

            Record record = recordRepository.findFirstByUserAndBook_Title(userCurrent, request.getTitle())
                    .orElseThrow(() -> new AppException(ErrorCode.NOT_BORROWED));
            if(!record.getStatus().equals("RETURNED"))
                throw new AppException(ErrorCode.NOT_ELIGIBLE_TO_EVALUATE);

            Evaluate evaluate = Evaluate.builder()
                    .user(userCurrent)
                    .title(request.getTitle())
                    .rating(request.getRating())
                    .comment(request.getComment())
                    .evaluateDay(LocalDate.now())
                    .build();
            evaluateRepository.save(evaluate);
            return toModel(evaluate);
        }
        throw new AppException(ErrorCode.UNAUTHORIZED);
    }

    public List<RatingCountResponse> countRating(String title) {
        return evaluateRepository.countRatingByTitle(title)
                .stream()
                .map(row -> new RatingCountResponse(
                        ((Number) row[0]).intValue(),
                        ((Number) row[1]).longValue()
                ))
                .toList();
    }

    public double averageRating(String title) {
        return Optional.ofNullable(evaluateRepository.averageRatingByTitle(title)).orElse(0.0);
    }

    public RatingSummaryResponse getRatingSummary(String title) {
        Object[] result = evaluateRepository.averageAndTotalRatingByTitle(title);
        if(result==null || result.length<2)
            return new RatingSummaryResponse(0.0, 0);
        double average = result[0] == null ? 0.0 : ((Number) result[0]).doubleValue();
        long total = result[1] == null ? 0L : ((Number) result[1]).longValue();

        return new RatingSummaryResponse(average, total);
    }
}
