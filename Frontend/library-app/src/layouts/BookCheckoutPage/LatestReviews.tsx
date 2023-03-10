import { Link } from "react-router-dom";
import ReviewModel from "../../models/ReviewModel";
import { Review } from "../Utils/Review";

export const LatestReviews: React.FC<{
  reviews: ReviewModel[];
  bookId: number | undefined;
  mobile: boolean;
}> = (props) => {
  return (
    <div className={props.mobile ? "mt-3" : "row mt-5"}>
      <div className={props.mobile ? "" : "col-sm-2 col-md-2"}>
        <h2>최근 리뷰 : </h2>
      </div>
      <div className="col-sm-10 col-md-10">
        {props.reviews.length > 0 ? (
          <>
            {props.reviews.slice(0, 3).map((eachReview) => (
              <Review review={eachReview} key={eachReview.id}></Review>
            ))}

            <div className="m-3">
              <Link
                type="button"
                className="btn main-color btn-md text-white"
                to={`/reviewlist/${props.bookId}`}
              >
                모든 리뷰 보기
              </Link>
            </div>
          </>
        ) : (
          <div className="m-3 mb-5">
            <p className="lead">리뷰가 없습니다. 첫 리뷰를 남겨보세요.</p>
          </div>
        )}
      </div>
    </div>
  );
};
