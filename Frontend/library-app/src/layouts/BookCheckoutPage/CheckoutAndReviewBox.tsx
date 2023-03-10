import { Link } from "react-router-dom";
import BookModel from "../../models/BookModel";
import { LeaveAReview } from "../Utils/LeaveAReview";

export const CheckOutAndReviewBox: React.FC<{
  book: BookModel | undefined;
  mobile: boolean;
  currentLoansCount: number;
  isAuthenticated: any;
  isCheckedOut: boolean;
  checkoutBook: any;
  isReviewLeft: boolean;
  submitReview: any;
}> = (props) => {
  function buttonRender() {
    if (props.isAuthenticated) {
      if (!props.isCheckedOut && props.currentLoansCount < 5) {
        return (
          <button
            onClick={() => props.checkoutBook()}
            className="btn btn-success btn-lg"
          >
            대여하기
          </button>
        );
      } else if (props.isCheckedOut) {
        return (
          <p>
            <b>책을 대여했습니다.</b>
          </p>
        );
      } else if (!props.isCheckedOut) {
        return <p className="text-danger">대여한 책이 너무 많습니다.</p>;
      }
    }
    return (
      <Link to={"/login"} className="btn btn-success btn-lg">
        로그인
      </Link>
    );
  }

  function reviewRender() {
    if (props.isAuthenticated && !props.isReviewLeft) {
      return (
        <>
          <LeaveAReview submitReview={props.submitReview} />
        </>
      );
    } else if (props.isAuthenticated && props.isReviewLeft) {
      return (
        <p>
          <b>리뷰를 남긴 도서입니다.</b>
        </p>
      );
    }
    return (
      <div>
        <hr />
        <p>로그인 후 리뷰를 남겨보세요!</p>
      </div>
    );
  }

  return (
    <div
      className={
        props.mobile ? "card d-flex mt-5" : "card col-3 container d-flex mb-5"
      }
    >
      <div className="card-body container">
        <div className="mt-3">
          <p>
            <b>{props.currentLoansCount}/5</b>
            나의 대여 권 수
          </p>
          <hr />
          {props.book &&
          props.book.copiesAvailable &&
          props.book.copiesAvailable > 0 ? (
            <h4 className="text-success">대여 가능함</h4>
          ) : (
            <h4 className="text-danger">대여 불가능</h4>
          )}
          <div className="row">
            <p className="col-6 lead">
              <b>대여 권 수 : {props.book?.copies}</b>
            </p>
            <p className="col-6 lead">
              <b>남은 권 수 : {props.book?.copiesAvailable}</b>
            </p>
          </div>
        </div>
        {buttonRender()}
        <hr />
        <p className="mt-3">책의 수량은 변경될 수 있습니다.</p>
        {reviewRender()}
      </div>
    </div>
  );
};
