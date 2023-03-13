import { Add, Clear, Remove } from "@mui/icons-material";
import { useOktaAuth } from "@okta/okta-react";
import { loadPaymentWidget } from "@tosspayments/payment-widget-sdk";
import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { tossConfig } from "../../../lib/tossConfig";
import BooksInCart from "../../../models/BooksInCart";
import { SpinnerLoading } from "../../Utils/SpinnerLoading";
import { deleteBookInCart } from "./PurchaseFunction";

export const Purchase = () => {
  const { authState } = useOktaAuth();
  const [httpError, setHttpError] = useState(null);
  const [selectedBooks, setSelectedBooks] = useState<number[]>([]);
  const [isCheckAll, setIsCheckAll] = useState(false);

  const [deleteBook, setDeleteBook] = useState(false);

  // Current Cart
  const [booksInCart, setBooksInCart] = useState<BooksInCart[]>([]);

  const handleCheckboxChange = (bookId: number) => {
    if (selectedBooks.includes(bookId)) {
      setSelectedBooks(selectedBooks.filter((id) => id !== bookId));
      setIsCheckAll(false);
    } else {
      setSelectedBooks([...selectedBooks, bookId]);
      if (selectedBooks.length + 1 === BooksInCart.length) {
        setIsCheckAll(true);
      }
    }
  };

  const changeAllCheck = (e: any) => {
    setSelectedBooks([]);
    if (e.target.checked) {
      setIsCheckAll(true);

      setSelectedBooks(booksInCart.map((bookInCart) => bookInCart.book.id));
    } else {
      setIsCheckAll(false);

      setSelectedBooks([]);
    }
  };

  const calculateTotalPrice = () => {
    let total = 0;
    selectedBooks.forEach((bookId) => {
      const selectedBook = booksInCart.find(
        (bookInCart) => bookInCart.book.id === bookId
      );
      if (selectedBook) {
        total += selectedBook.book.price * selectedBook.amount;
      }
    });
    return total;
  };
  const selectedBooksArr = () => {
    let booksArr: string[] = [];
    selectedBooks.forEach((bookId) => {
      const selectedBook = booksInCart.find(
        (bookInCart) => bookInCart.book.id === bookId
      );
      if (selectedBook) {
        booksArr.push(selectedBook.book.title);
      }
    });
    return booksArr;
  };

  const tossPay = async () => {
    const paymentWidget = await loadPaymentWidget(
      tossConfig.clientKey,
      tossConfig.customerKey
    );
    paymentWidget.renderPaymentMethods(
      "#payment-method",
      calculateTotalPrice()
    );

    const userEmail = authState?.accessToken?.claims.sub;
    const userName = authState?.accessToken?.claims.name;
    let books = selectedBooksArr();
    paymentWidget.requestPayment({
      orderId: "AD8aZDpbzXs4EQa-UkIX6",
      orderName: `${books[0]}외 ${books.length - 1}건`,
      successUrl: "https://localhost:8443/success",
      failUrl: "https://localhost:8443/fail",
      customerEmail: userEmail,
      customerName: userName,
    });
  };

  const [isLoadingBooksInCart, setIsLoadingBooksInCart] = useState(true);

  useEffect(() => {
    const fetchBooksInCart = async () => {
      if (authState && authState.isAuthenticated) {
        const url = `${process.env.REACT_APP_API}/books/secure/currentcart`;
        const requestOptions = {
          methoe: "GET",
          headers: {
            Authorization: `Bearer ${authState.accessToken?.accessToken}`,
            "Content-Type": "application/json",
          },
        };
        const booksInCartResponse = await fetch(url, requestOptions);
        if (!booksInCartResponse.ok) {
          throw new Error("Something went wrong!");
        }
        const booksInCartResponseJson = await booksInCartResponse.json();
        setBooksInCart(booksInCartResponseJson);
      }
      setIsLoadingBooksInCart(false);
      setDeleteBook(false);
    };
    fetchBooksInCart().catch((error: any) => {
      setIsLoadingBooksInCart(false);
      setHttpError(error.message);
    });
    window.scrollTo(0, 0);
  }, [authState, deleteBook]);

  const deleteFetchBook = async (bookId: number) => {
    deleteBookInCart(bookId, authState);
    setDeleteBook(true);
  };

  if (isLoadingBooksInCart) {
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
      <div id="payment-method"></div>
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
        {booksInCart.length > 0 ? (
          <>
            {booksInCart.map((bookInCart) => (
              <div key={bookInCart.book.id}>
                <div className="row mt-3 mb-2">
                  <div className="d-flex col-md-8">
                    <input
                      id="selectedBook"
                      type="checkbox"
                      onChange={() => handleCheckboxChange(bookInCart.book.id)}
                      checked={selectedBooks.includes(bookInCart.book.id)}
                    />
                    <div className="col col-md-3">
                      <img
                        src={bookInCart.book?.img}
                        width="150"
                        height="210"
                        alt="Book"
                        className="shadow bg-body-tertiary m-2"
                      />
                    </div>
                    <div className="card-body m-2">
                      <div className="card-title d-flex justify-content-between">
                        <h4>
                          <Link
                            style={{ color: "black", textDecoration: "none" }}
                            to={`/checkout/${bookInCart.book.id}`}
                          >
                            {bookInCart.book.title}
                          </Link>
                        </h4>
                        <p
                          className="card-text"
                          onClick={() => deleteFetchBook(bookInCart.book.id)}
                        >
                          삭제
                        </p>
                      </div>
                      <div className="card-text">
                        <h5>{bookInCart.book.author}</h5>
                      </div>

                      <div className="card-text">
                        <p>
                          {bookInCart.book.publisher} .
                          {bookInCart.book.publicationDate}
                        </p>
                      </div>
                      <div className="cart-text input-group">
                        <div className="input-group-prepend">
                          <button className="btn btn-warning" type="button">
                            <Remove />
                          </button>
                        </div>
                        <div className="col-1">
                          <input
                            type="text"
                            className="form-control text-center"
                            value={bookInCart.amount}
                            readOnly
                          />
                        </div>
                        <div className="input-group-append">
                          <button className="btn btn-warning" type="button">
                            <Add />
                          </button>
                        </div>
                      </div>
                    </div>
                  </div>
                  <div
                    className="col-md-3 container d-flex"
                    style={{ textAlign: "center" }}
                  >
                    <div className="card-body">
                      <div className="mt-5">
                        <h4>₩ {bookInCart.book.price * bookInCart.amount}</h4>
                      </div>
                      <hr />
                      <Link
                        className="btn btn-primary"
                        to={`/checkout/${bookInCart.book.id}`}
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
              <h4 className="m-4">총 금액 | ₩ {calculateTotalPrice()} </h4>
            </div>
            <button className="btn btn-primary btn-lg m-2" onClick={tossPay}>
              결제하기
            </button>
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
        {booksInCart.length > 0 ? (
          <>
            {booksInCart.map((bookInCart) => (
              <div key={bookInCart.book.id} style={{ textAlign: "center" }}>
                <input
                  id="selectedBook"
                  type="checkbox"
                  onChange={() => handleCheckboxChange(bookInCart.book.id)}
                  checked={selectedBooks.includes(bookInCart.book.id)}
                />
                <div className="d-flex justify-content-center align-items-center">
                  <img
                    src={bookInCart.book?.img}
                    width="150"
                    height="210"
                    alt="Book"
                    className="shadow bg-body-tertiary m-2"
                  />
                </div>
                <div className="card-body">
                  <div className="card-title d-flex justify-content-between">
                    <h4>
                      <Link
                        style={{ color: "black", textDecoration: "none" }}
                        to={`/checkout/${bookInCart.book.id}`}
                      >
                        {bookInCart.book.title}
                      </Link>
                    </h4>
                    <p
                      className="card-text"
                      onClick={() => deleteFetchBook(bookInCart.book.id)}
                    >
                      삭제
                      <Clear />
                    </p>
                  </div>
                  <div className="card-title">
                    <h5>{bookInCart.book.author}</h5>
                  </div>
                  <div className="card-title">
                    <p>
                      {bookInCart.book.publisher} .{" "}
                      {bookInCart.book.publicationDate}
                    </p>
                  </div>
                </div>
                <div className="card d-flex mt-5 mb-3">
                  <div className="card-body container">
                    <div className="mt-3">
                      <h4>₩ {bookInCart.book.price}</h4>
                    </div>
                    <hr />
                    <Link
                      className="btn btn-primary"
                      to={`/checkout/${bookInCart.book.id}`}
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
                  <h4 className="m-4">총 금액 | ₩ {calculateTotalPrice()} </h4>
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
