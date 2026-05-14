package umc.domain.review.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import umc.domain.member.entity.Member;
import umc.domain.member.exception.MemberException;
import umc.domain.member.exception.code.MemberErrorCode;
import umc.domain.member.repository.MemberRepository;
import umc.domain.mission.exception.MissionException;
import umc.domain.mission.exception.code.MissionErrorCode;
import umc.domain.review.converter.ReviewConverter;
import umc.domain.review.dto.ReviewReqDTO;
import umc.domain.review.dto.ReviewResDTO;
import umc.domain.review.entity.Review;
import umc.domain.review.exception.ReviewException;
import umc.domain.review.exception.code.ReviewErrorCode;
import umc.domain.review.repository.ReviewRepository;
import umc.domain.store.entity.Store;
import umc.domain.store.exception.StoreException;
import umc.domain.store.exception.code.StoreErrorCode;
import umc.domain.store.repository.StoreRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MemberRepository memberRepository;
    private final StoreRepository storeRepository;

    @Transactional
    public ReviewResDTO.CreateReview createReview(
            Long storeId,
            Long memberId,
            ReviewReqDTO.CreateReview request
    ) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new MemberException(MemberErrorCode.MEMBER_NOT_FOUND));

        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new StoreException(StoreErrorCode.STORE_NOT_FOUND));

        Review review = Review.builder()
                .content(request.content())
                .rating(request.rating())
                .member(member)
                .store(store)
                .build();

        Review savedReview = reviewRepository.save(review);

        return ReviewConverter.toCreateReview(savedReview);
    }

    @Transactional(readOnly = true)
    public ReviewResDTO.PaginationCursor<ReviewResDTO.GetMyReview> getMyReviews(
            Long memberId,
            Integer pageSize,
            String cursor,
            String query
    ) {

        PageRequest pageRequest = PageRequest.of(0, pageSize);

        long idCursor;
        int ratingCursor;
        Slice<Review> reviewList;
        String nextCursor;
        List<Review> content;
        Review lastReview;

        if(!cursor.equals("-1")){

            String[] cursorSplit = cursor.split(":");
            switch(query.toLowerCase()){
                case "id":

                    idCursor = Long.parseLong(cursorSplit[1]);

                    reviewList = reviewRepository.findByMember_IdAndIdLessThanOrderByIdDesc(
                            memberId,
                            idCursor,
                            pageRequest
                    );
                    content = reviewList.getContent();
                    lastReview = content.get(content.size() - 1);
                    nextCursor = lastReview.getId() + ":" + lastReview.getId();
                    break;

                case "rating":

                    ratingCursor = Integer.parseInt(cursorSplit[0]);
                    idCursor = Long.parseLong(cursorSplit[1]);

                    reviewList = reviewRepository.findNextByMemberIdAndRatingCursor(memberId, ratingCursor, idCursor, pageRequest);

                    content = reviewList.getContent();
                    lastReview = content.get(content.size() - 1);
                    nextCursor = lastReview.getRating() + ":" + lastReview.getId();

                    break;

                default:
                    throw new ReviewException(ReviewErrorCode.QUERY_NOT_VALID);
            }
        }
        else{
            switch(query.toLowerCase()){
                case "id":

                    reviewList = reviewRepository.findReviewsByMember_IdOrderByIdDesc(memberId, pageRequest);
                    content = reviewList.getContent();
                    lastReview = content.get(content.size() - 1);
                    nextCursor = lastReview.getId() + ":" + lastReview.getId();
                    break;

                case "rating":

                    reviewList = reviewRepository.findFirstByMemberIdOrderByRating(memberId, pageRequest);

                    content = reviewList.getContent();
                    lastReview = content.get(content.size() - 1);
                    nextCursor = lastReview.getRating() + ":" + lastReview.getId();

                    break;
                default:
                    throw new ReviewException(ReviewErrorCode.QUERY_NOT_VALID);
            }
        }


        return ReviewConverter.toPaginationCursor(
                reviewList.map(ReviewConverter::toGetMyReview).toList(),
                reviewList.hasNext(),
                nextCursor,
                reviewList.getSize()
        );
    }
}
