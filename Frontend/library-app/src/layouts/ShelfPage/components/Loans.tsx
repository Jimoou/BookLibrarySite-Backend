import { Bolt } from "@mui/icons-material";
import { useOktaAuth } from "@okta/okta-react";
import { useEffect, useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import ShelfCurrentLoans from "../../../models/ShelfCurrentLoans";
import { addBookInCart } from "../../CartPage/components/PaymentFunction";
import { SpinnerLoading } from "../../Utils/SpinnerLoading";
import { LoansModal } from "./LoansModal";

export const Loans = () => {
  const { authState } = useOktaAuth();
  const [httpError, setHttpError] = useState(null);

  // Current Loans
  const [shelfCurrentLoans, setShelfCurrentLoans] = useState<
    ShelfCurrentLoans[]
  >([]);
  const [checkout, setCheckout] = useState(false);

  const [isLoadingUserLoans, setIsLoadingUserLoans] = useState(true);

  const [isOpen, setIsOpen] = useState(false);
  const navigate = useNavigate();
  const confirm = () => {
    setIsOpen(false);
    navigate("/cart");
  };

  const cancel = () => {
    setIsOpen(false);
  };

  const handleClick = async (bookId: number) => {
    await addBookInCart(bookId, authState);
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

  useEffect(() => {
    const fetchUserCurrentLoans = async () => {
      if (authState && authState.isAuthenticated) {
        const url = `${process.env.REACT_APP_API}/books/secure/currentloans`;
        const requestOptions = {
          methoe: "GET",
          headers: {
            Authorization: `Bearer ${authState.accessToken?.accessToken}`,
            "Content-Type": "application/json",
          },
        };
        const shelfCurrentLoansResponse = await fetch(url, requestOptions);
        if (!shelfCurrentLoansResponse.ok) {
          throw new Error("Something went wrong!");
        }
        const shelfCurrentLoansResponseJson =
          await shelfCurrentLoansResponse.json();
        setShelfCurrentLoans(shelfCurrentLoansResponseJson);
      }
      setIsLoadingUserLoans(false);
    };
    fetchUserCurrentLoans().catch((error: any) => {
      setIsLoadingUserLoans(false);
      setHttpError(error.message);
    });
    window.scrollTo(0, 0);
  }, [authState, checkout]);

  if (isLoadingUserLoans) {
    return <SpinnerLoading />;
  }

  if (httpError) {
    return (
      <div className="container m-5">
        <p>{httpError}</p>
      </div>
    );
  }

  async function returnBook(bookId: number) {
    const url = `${process.env.REACT_APP_API}/books/secure/return/?bookId=${bookId}`;
    const requestOptions = {
      method: "PUT",
      headers: {
        Authorization: `Bearer ${authState?.accessToken?.accessToken}`,
        "Content-Type": "application/json",
      },
    };
    const returnResponse = await fetch(url, requestOptions);
    if (!returnResponse.ok) {
      throw new Error("Something went wrong!");
    }
    setCheckout(!checkout);
  }

  async function renewLoan(bookId: number) {
    const url = `${process.env.REACT_APP_API}/books/secure/renew/loan/?bookId=${bookId}`;
    const requestOptions = {
      method: "PUT",
      headers: {
        Authorization: `Bearer ${authState?.accessToken?.accessToken}`,
        "Content-Type": "application/json",
      },
    };

    const returnResponse = await fetch(url, requestOptions);
    if (!returnResponse.ok) {
      throw new Error("Something went wrong!");
    }
    setCheckout(!checkout);
  }

  return (
    <div>
      {isOpen && renderModal()}
      {/* Desktop */}
      <div className="d-none d-lg-block mt-2">
        {shelfCurrentLoans.length > 0 ? (
          <>
            {shelfCurrentLoans.map((shelfCurrentLoan) => (
              <div key={shelfCurrentLoan.book.id}>
                <div className="row mt-3 mb-2">
                  <div className="d-flex col-md-8">
                    <div className="col col-md-3">
                      <img
                        src={shelfCurrentLoan.book?.img}
                        width="150"
                        height="210"
                        alt="Book"
                        className="shadow bg-body-tertiary m-2"
                      />
                    </div>
                    <div className="card-body m-2">
                      <div className="card-title">
                        <h4>{shelfCurrentLoan.book.title}</h4>
                      </div>
                      <div className="card-text">
                        <h5>{shelfCurrentLoan.book.author}</h5>
                      </div>
                      <div className="card-text">
                        <p>
                          {shelfCurrentLoan.book.publisher} .
                          {shelfCurrentLoan.book.publicationDate}
                        </p>
                      </div>
                      <div className="card-text">
                        <h5>{shelfCurrentLoan.book.price} 원</h5>
                      </div>
                      <div className="card-text">
                        <h5>
                          {shelfCurrentLoan.book.coin}{" "}
                          <Bolt style={{ color: "yellow" }} />
                        </h5>
                      </div>
                    </div>
                  </div>
                  <div className="card col-md-3 container d-flex">
                    <div className="card-body">
                      <div className="mt-2">
                        <h4>책 관리</h4>
                        {shelfCurrentLoan.daysLeft > 0 && (
                          <p className="text-secondary">
                            반납까지 {shelfCurrentLoan.daysLeft} 일 남았습니다.
                          </p>
                        )}
                        {shelfCurrentLoan.daysLeft === 0 && (
                          <p className="text-success">반납일입니다.</p>
                        )}
                        {shelfCurrentLoan.daysLeft < 0 && (
                          <p className="text-danger">
                            반납일이 {shelfCurrentLoan.daysLeft} 일 지났습니다.
                          </p>
                        )}
                        <div className="list-group mt-3">
                          <button
                            className="list-group-item list-group-item-action"
                            aria-current="true"
                            data-bs-toggle="modal"
                            data-bs-target={`#modal${shelfCurrentLoan.book.id}`}
                          >
                            대여 서비스
                          </button>
                        </div>
                      </div>
                      <hr />
                      <Link
                        className="btn btn-primary"
                        to={`/checkout/${shelfCurrentLoan.book.id}`}
                      >
                        리뷰 쓰러 가기
                      </Link>
                      <button
                        className="btn btn-warning m-1"
                        onClick={() => handleClick(shelfCurrentLoan.book.id)}
                      >
                        장바구니에 담기
                      </button>
                    </div>
                  </div>
                </div>
                <hr />
                <LoansModal
                  shelfCurrentLoan={shelfCurrentLoan}
                  mobile={false}
                  returnbook={returnBook}
                  renewLoan={renewLoan}
                />
              </div>
            ))}
          </>
        ) : (
          <>
            <h3 className="mt-3">대여중인 책이 없습니다.</h3>
            <Link className="btn btn-primary" to={`search`}>
              책 찾아보기
            </Link>
          </>
        )}
      </div>

      {/* Mobile */}
      <div className="container d-lg-none mt-2">
        {shelfCurrentLoans.length > 0 ? (
          <>
            {shelfCurrentLoans.map((shelfCurrentLoan) => (
              <div
                key={shelfCurrentLoan.book.id}
                style={{ textAlign: "center" }}
              >
                <div className="d-flex justify-content-center align-items-center">
                  <img
                    src={shelfCurrentLoan.book?.img}
                    width="150"
                    height="210"
                    alt="Book"
                    className="shadow bg-body-tertiary m-2"
                  />
                </div>
                <div className="card-body">
                  <div className="card-title">
                    <h4>{shelfCurrentLoan.book.title}</h4>
                  </div>
                  <div className="card-title">
                    <h5>{shelfCurrentLoan.book.author}</h5>
                  </div>
                  <div className="card-title">
                    <p>
                      {shelfCurrentLoan.book.publisher} .{" "}
                      {shelfCurrentLoan.book.publicationDate}
                    </p>
                  </div>
                  <div className="card-text">
                    <h5>{shelfCurrentLoan.book.price} 원</h5>
                  </div>
                  <div className="card-text">
                    <h5>
                      {shelfCurrentLoan.book.coin}{" "}
                      <Bolt style={{ color: "yellow" }} />
                    </h5>
                  </div>
                </div>
                <div className="card d-flex mt-5 mb-3">
                  <div className="card-body container">
                    <div className="mt-3">
                      <h4>책 관리</h4>
                      {shelfCurrentLoan.daysLeft > 0 && (
                        <p className="text-secondary">
                          반납까지 {shelfCurrentLoan.daysLeft} 일 남았습니다.
                        </p>
                      )}
                      {shelfCurrentLoan.daysLeft === 0 && (
                        <p className="text-success">반납일입니다.</p>
                      )}
                      {shelfCurrentLoan.daysLeft < 0 && (
                        <p className="text-danger">
                          반납일이 {shelfCurrentLoan.daysLeft} 일 지났습니다.
                        </p>
                      )}
                      <div className="list-group mt-3">
                        <button
                          className="list-group-item list-group-item-action"
                          aria-current="true"
                          data-bs-toggle="modal"
                          data-bs-target={`#mobilemodal${shelfCurrentLoan.book.id}`}
                        >
                          대여 서비스
                        </button>
                      </div>
                    </div>
                    <hr />
                    <Link
                      className="btn btn-primary"
                      to={`/checkout/${shelfCurrentLoan.book.id}`}
                    >
                      리뷰 쓰러 가기
                    </Link>
                    <button
                      className="btn btn-warning m-1"
                      onClick={() => handleClick(shelfCurrentLoan.book.id)}
                    >
                      장바구니에 담기
                    </button>
                  </div>
                </div>
                <hr />
                <LoansModal
                  shelfCurrentLoan={shelfCurrentLoan}
                  mobile={true}
                  returnbook={returnBook}
                  renewLoan={renewLoan}
                />
              </div>
            ))}
          </>
        ) : (
          <>
            <h3 className="mt-3">대여중인 책이 없습니다.</h3>
            <Link className="btn btn-primary" to={`search`}>
              책 찾아보기
            </Link>
          </>
        )}
      </div>
    </div>
  );
};
