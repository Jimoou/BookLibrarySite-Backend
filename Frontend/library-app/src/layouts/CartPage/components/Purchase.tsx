import { Bolt } from "@mui/icons-material";
import { useOktaAuth } from "@okta/okta-react";
import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import ShelfCurrentLoans from "../../../models/ShelfCurrentLoans";
import { SpinnerLoading } from "../../Utils/SpinnerLoading";
// import { LoansModal } from "./LoansModal";

export const Purchase = () => {
  const { authState } = useOktaAuth();
  const [httpError, setHttpError] = useState(null);
  const [selectedBooks, setSelectedBooks] = useState<number[]>([]);
  const [isCheckAll, setIsCheckAll] = useState(false);
  const [isCheck, setIsCheck] = useState(false);

  // Current Loans
  const [shelfCurrentLoans, setShelfCurrentLoans] = useState<
    ShelfCurrentLoans[]
  >([]);

  const handleCheckboxChange = (bookId: number) => {
    if (selectedBooks.includes(bookId)) {
      setSelectedBooks(selectedBooks.filter((id) => id !== bookId));
      setIsCheckAll(false);
    } else {
      setSelectedBooks([...selectedBooks, bookId]);
      if (selectedBooks.length + 1 === shelfCurrentLoans.length) {
        setIsCheckAll(true);
      }
    }
  };

  const changeAllCheck = (e: any) => {
    setSelectedBooks([]);
    if (e.target.checked) {
      setIsCheckAll(true);
      setIsCheck(true);
      setSelectedBooks(
        shelfCurrentLoans.map((shelfCurrentLoan) => shelfCurrentLoan.book.id)
      );
    } else {
      setIsCheckAll(false);
      setIsCheck(false);
      setSelectedBooks([]);
    }
  };

  const calculateTotalPrice = () => {
    let total = 0;
    selectedBooks.forEach((bookId) => {
      const selectedBook = shelfCurrentLoans.find(
        (shelfCurrentLoan) => shelfCurrentLoan.book.id === bookId
      );
      if (selectedBook) {
        total += selectedBook.book.price;
      }
    });
    return `₩ ${total}`;
  };
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

  return (
    <div>
      {/* Desktop */}
      <div className="d-none d-lg-block mt-2">
        <h5>
          <input
            id="checkAll"
            type="checkbox"
            onChange={(e) => changeAllCheck(e)}
            checked={isCheckAll}
          />{" "}
          <label htmlFor="checkAll">전체선택</label>
        </h5>
        {shelfCurrentLoans.length > 0 ? (
          <>
            {shelfCurrentLoans.map((shelfCurrentLoan) => (
              <div key={shelfCurrentLoan.book.id}>
                <div className="row mt-3 mb-2">
                  <div className="d-flex col-md-8">
                    <input
                      id="selectedBook"
                      type="checkbox"
                      onChange={() =>
                        handleCheckboxChange(shelfCurrentLoan.book.id)
                      }
                      checked={selectedBooks.includes(shelfCurrentLoan.book.id)}
                    />
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
                        <h4>
                          <Link
                            style={{ color: "black", textDecoration: "none" }}
                            to={`/checkout/${shelfCurrentLoan.book.id}`}
                          >
                            {shelfCurrentLoan.book.title}
                          </Link>
                        </h4>
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
                    </div>
                  </div>
                  <div
                    className="col-md-3 container d-flex"
                    style={{ textAlign: "center" }}
                  >
                    <div className="card-body">
                      <div className="mt-5">
                        <h4>₩ {shelfCurrentLoan.book.price}</h4>
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
              </div>
            ))}
          </>
        ) : (
          <>
            <h3 className="mt-3">장바구니에 담긴 책이 없습니다.</h3>
            <Link className="btn btn-primary" to={`search`}>
              책 찾아보기
            </Link>
          </>
        )}
        <div className="col-md-12 mb-3">
          <div className="d-flex justify-content-end p-3 total-price-container">
            <div className="d-flex align-items-center">
              <h4 className="m-4">총 금액 | {calculateTotalPrice()} </h4>
            </div>
            <button className="btn btn-primary btn-lg m-2">결제하기</button>
          </div>
        </div>
      </div>

      {/* Mobile */}
      <div className="container d-lg-none mt-2">
        <h5>
          <input
            id="checkAll"
            type="checkbox"
            onChange={(e) => changeAllCheck(e)}
            checked={isCheckAll}
          />{" "}
          <label htmlFor="checkAll">전체선택</label>
        </h5>
        {shelfCurrentLoans.length > 0 ? (
          <>
            {shelfCurrentLoans.map((shelfCurrentLoan) => (
              <div
                key={shelfCurrentLoan.book.id}
                style={{ textAlign: "center" }}
              >
                <input
                  id="selectedBook"
                  type="checkbox"
                  onChange={() =>
                    handleCheckboxChange(shelfCurrentLoan.book.id)
                  }
                  checked={selectedBooks.includes(shelfCurrentLoan.book.id)}
                />
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
                    <h4>
                      <Link
                        style={{ color: "black", textDecoration: "none" }}
                        to={`/checkout/${shelfCurrentLoan.book.id}`}
                      >
                        {shelfCurrentLoan.book.title}
                      </Link>
                    </h4>
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
                </div>
                <div className="card d-flex mt-5 mb-3">
                  <div className="card-body container">
                    <div className="mt-3">
                      <h4>₩ {shelfCurrentLoan.book.price}</h4>
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
              </div>
            ))}
            <div className="col-md-12 mb-3">
              <div className="d-flex justify-content-end p-3 total-price-container">
                <div className="d-flex align-items-center">
                  <h4 className="m-4">총 금액 | {calculateTotalPrice()} </h4>
                </div>
                <button className="btn btn-primary btn-lg m-2">결제하기</button>
              </div>
            </div>
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
