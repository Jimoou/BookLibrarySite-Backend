import { useOktaAuth } from "@okta/okta-react";
import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import ShelfCurrentLoans from "../../../models/ShelfCurrentLoans";
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
      {/* Desktop */}
      <div className="d-none d-lg-block mt-2">
        {shelfCurrentLoans.length > 0 ? (
          <>
            <h5>대여중인 책 : </h5>

            {shelfCurrentLoans.map((shelfCurrentLoan) => (
              <div key={shelfCurrentLoan.book.id}>
                <div className="row mt-3 mb-3">
                  <div className="col-4 col-md-4 container">
                    <img
                      src={shelfCurrentLoan.book?.img}
                      width="226"
                      height="349"
                      alt="Book"
                    />
                  </div>
                  <div className="card col-3 col-md-3 container d-flex">
                    <div className="card-body">
                      <div className="mt-5">
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
            <h5 className="mb-3">대여중인 책: </h5>

            {shelfCurrentLoans.map((shelfCurrentLoan) => (
              <div key={shelfCurrentLoan.book.id}>
                <div className="d-flex justify-content-center align-items-center">
                  <img
                    src={shelfCurrentLoan.book?.img}
                    width="226"
                    height="349"
                    alt="Book"
                  />
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
