import { useOktaAuth } from "@okta/okta-react";
import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
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
  addBookInCart: any;
}> = (props) => {
  const [isOpen, setIsOpen] = useState(false);
  const navigate = useNavigate();
  const { authState } = useOktaAuth();
  const confirm = () => {
    setIsOpen(false);
    navigate("/cart");
  };

  const cancel = () => {
    setIsOpen(false);
  };

  const handleClick = () => {
    props.addBookInCart();
    setIsOpen(true);
  };

  const renderModal = () => {
    return (
      <div
        className="modal fade show"
        style={{ display: "block" }}
        tabIndex={-1}
        aria-modal="true"
      >
        <div className="modal-dialog">
          <div className="modal-content">
            <div className="modal-body">
              장바구니에 추가되었습니다. 장바구니로 바로 이동하시겠습니까?
            </div>
            <div className="modal-footer">
              <button className="btn btn-secondary" onClick={cancel}>
                취소
              </button>
              <button className="btn btn-primary" onClick={confirm}>
                이동
              </button>
            </div>
          </div>
        </div>
      </div>
    );
  };

  function buttonRender() {
    if (props.isAuthenticated) {
      if (!props.isCheckedOut && props.currentLoansCount < 5) {
        return (
          <button
            onClick={() => props.checkoutBook()}
            className="btn btn-success"
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
        return <p className="text-danger">더이상 대여할 수 없습니다.</p>;
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
        <h4 className="text-success">구매하기</h4>
        <div className="row">
          <p className="col-8 lead">
            <b>가격 : {props.book?.price} 원</b>
          </p>
        </div>

        {authState?.isAuthenticated && (
          <>
            <button className="btn btn-info">바로구매</button>
            <button className="btn btn-warning m-1" onClick={handleClick}>
              장바구니
            </button>
          </>
        )}

        {isOpen && renderModal()}
        <hr />
        <p className="mt-3">책의 수량은 변경될 수 있습니다.</p>
        {reviewRender()}
      </div>
    </div>
  );
};
