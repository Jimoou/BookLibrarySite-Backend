import ShelfCurrentLoans from "../../../models/ShelfCurrentLoans";

export const LoansModal: React.FC<{
  shelfCurrentLoan: ShelfCurrentLoans;
  mobile: boolean;
  returnbook: any;
  renewLoan: any;
}> = (props) => {
  return (
    <div
      className="modal fade"
      id={
        props.mobile
          ? `mobilemodal${props.shelfCurrentLoan.book.id}`
          : `modal${props.shelfCurrentLoan.book.id}`
      }
      data-bs-backdrop="static"
      data-bs-keyboard="false"
      aria-labelledby="staticBackdropLabel"
      aria-hidden="true"
      key={props.shelfCurrentLoan.book.id}
    >
      <div className="modal-dialog">
        <div className="modal-content">
          <div className="modal-header">
            <h5 className="modal-title" id="staticBackdropLabel">
              대여 서비스
            </h5>
            <button
              type="button"
              className="btn-close"
              data-bs-dismiss="modal"
              aria-label="Close"
            ></button>
          </div>
          <div className="modal-body">
            <div className="container">
              <div className="mt-3">
                <div className="row">
                  <div className="col-2">
                    <img
                      src={props.shelfCurrentLoan.book?.img}
                      width="56"
                      height="87"
                      alt="Book"
                    />
                  </div>
                  <div className="col-10">
                    <h6>{props.shelfCurrentLoan.book.author}</h6>
                    <h4>{props.shelfCurrentLoan.book.title}</h4>
                  </div>
                </div>
                <hr />
                {props.shelfCurrentLoan.daysLeft > 0 && (
                  <p className="text-secondary">
                    반납까지 {props.shelfCurrentLoan.daysLeft} 일 남았습니다.
                  </p>
                )}
                {props.shelfCurrentLoan.daysLeft === 0 && (
                  <p className="text-success">반납일입니다.</p>
                )}
                {props.shelfCurrentLoan.daysLeft < 0 && (
                  <p className="text-danger">
                    반납일이 {props.shelfCurrentLoan.daysLeft} 일 지났습니다.
                  </p>
                )}
                <div className="list-group mt-3">
                  <button
                    onClick={() =>
                      props.returnbook(props.shelfCurrentLoan.book.id)
                    }
                    data-bs-dismiss="modal"
                    className="list-group-item list-group-item-action"
                    aria-current="true"
                  >
                    책 반납하기
                  </button>
                  <button
                    onClick={() =>
                      props.renewLoan(props.shelfCurrentLoan.book.id)
                    }
                    data-bs-dismiss="modal"
                    className={
                      props.shelfCurrentLoan.daysLeft < 0
                        ? "list-group-item list-group-item-action disabled"
                        : "list-group-item list-group-item-action"
                    }
                  >
                    {props.shelfCurrentLoan.daysLeft < 0
                      ? "연체 되어 반납일을 갱신할 수 없습니다."
                      : "반납일 연장하기"}
                  </button>
                </div>
              </div>
            </div>
          </div>
          <div className="modal-footer">
            <button
              type="button"
              className="btn btn-secondary"
              data-bs-dismiss="modal"
            >
              닫기
            </button>
          </div>
        </div>
      </div>
    </div>
  );
};
