import { Add, Clear, Remove } from "@mui/icons-material";
import { useOktaAuth } from "@okta/okta-react";
import { loadPaymentWidget } from "@tosspayments/payment-widget-sdk";
import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { tossConfig } from "../../../lib/tossConfig";
import PendingPaymentRequest from "../../../models/PendingPaymentRequest";
import CurrentCartItems from "../../../models/CurrentCartItems";
import { SpinnerLoading } from "../../Utils/SpinnerLoading";
import {
  decreaseAmount,
  deleteBookInCart,
  increaseAmount,
} from "./PaymentFunction";

export const CartItem = () => {
  const { authState } = useOktaAuth();
  const [httpError, setHttpError] = useState(null);
  const [selectedBooks, setSelectedBooks] = useState<number[]>([]);
  const [isCheckAll, setIsCheckAll] = useState(false);

  const [deleteBook, setDeleteBook] = useState(false);
  const [changeAmount, setChangeAmount] = useState(false);

  const [cartItems, setCartItems] = useState<CurrentCartItems[]>([]);
  const [isLoadingCartItems, setIsLoadingCartItems] = useState(true);

  const [paymentBooks, setPaymentBooks] = useState<PendingPaymentRequest[]>([]);
  const [selectedBooksArr, setSelectedBooksArr] = useState<string[]>([]);

  const handleCheckboxChange = (bookId: number) => {
    if (selectedBooks.includes(bookId)) {
      setSelectedBooks(selectedBooks.filter((id) => id !== bookId));
      setIsCheckAll(false);
    } else {
      setSelectedBooks([...selectedBooks, bookId]);
      if (selectedBooks.length + 1 === cartItems.length) {
        setIsCheckAll(true);
      }
    }
  };

  const changeAllCheck = (e: any) => {
    setSelectedBooks([]);
    if (e.target.checked) {
      setIsCheckAll(true);

      setSelectedBooks(cartItems.map((cartItem) => cartItem.book.id));
    } else {
      setIsCheckAll(false);

      setSelectedBooks([]);
    }
  };

  const calculateTotalPrice = () => {
    let total = 0;
    selectedBooks.forEach((bookId) => {
      const selectedBook = cartItems.find(
        (cartItem) => cartItem.book.id === bookId
      );
      if (selectedBook) {
        total += selectedBook.book.price * selectedBook.amount;
      }
    });
    return total;
  };

  useEffect(() => {
    let booksArr: string[] = [];
    let booksObjArr: PendingPaymentRequest[] = [];

    selectedBooks.forEach((bookId) => {
      const selectedBook = cartItems.find(
        (cartItem) => cartItem.book.id === bookId
      );
      if (selectedBook) {
        booksArr.push(selectedBook.book.title);
        booksObjArr.push(
          new PendingPaymentRequest(
            selectedBook.book.id,
            selectedBook.amount,
            selectedBook.cartItemId
          )
        );
      }
    });
    setPaymentBooks(booksObjArr);
    setSelectedBooksArr(booksArr);
  }, [cartItems, selectedBooks]);

  const addPaymentHistory = async (
    addPaymentHistoryRequest: PendingPaymentRequest[]
  ) => {
    if (authState && authState.isAuthenticated) {
      const url = `${process.env.REACT_APP_API}/payment/secure`;
      const requestOptions = {
        method: "POST",
        headers: {
          Authorization: `Bearer ${authState.accessToken?.accessToken}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify(addPaymentHistoryRequest),
      };
      const response = await fetch(url, requestOptions);
      if (!response.ok) {
        throw new Error("Something went wrong!");
      }
    }
  };

  const deleteFailPayment = async () => {
    if (authState && authState.isAuthenticated) {
      const url = `${process.env.REACT_APP_API}/payment/secure/delete/fail`;
      const requestOptions = {
        method: "DELETE",
        headers: {
          Authorization: `Bearer ${authState.accessToken?.accessToken}`,
          "Content-Type": "application/json",
        },
      };
      const response = await fetch(url, requestOptions);
      if (!response.ok) {
        throw new Error("Something went wrong!");
      }
    }
  };

  const generateOrderId = () => {
    const characters =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_=";
    const charactersLength = characters.length;
    let result = "";

    for (let i = 0; i < 15; i++) {
      result += characters.charAt(Math.floor(Math.random() * charactersLength));
    }

    return result;
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

    addPaymentHistory(paymentBooks);

    let orderName =
      selectedBooksArr.length > 1
        ? `${selectedBooksArr[0]}외 ${selectedBooksArr.length - 1}건`
        : selectedBooksArr[0];
    const generatedOrderId = generateOrderId();
    paymentWidget
      .requestPayment({
        orderId: generatedOrderId,
        orderName: orderName,
        successUrl: "https://localhost:3000/success",
        failUrl: "https://localhost:3000/fail",
        customerEmail: userEmail,
        customerName: userName,
      })
      .catch((error) => {
        setHttpError(error.message);
        deleteFailPayment();
      });
  };

  useEffect(() => {
    const fetchCartItems = async () => {
      if (authState && authState.isAuthenticated) {
        const url = `${process.env.REACT_APP_API}/cart/secure`;
        const requestOptions = {
          method: "GET",
          headers: {
            Authorization: `Bearer ${authState.accessToken?.accessToken}`,
            "Content-Type": "application/json",
          },
        };
        const CurrentCartItemResponse = await fetch(url, requestOptions);
        if (!CurrentCartItemResponse.ok) {
          throw new Error("Something went wrong!");
        }
        const CurrentCartItemResponseJson =
          await CurrentCartItemResponse.json();
        setCartItems(CurrentCartItemResponseJson);
      }
      setIsLoadingCartItems(false);
      setDeleteBook(false);
      setChangeAmount(false);
    };
    fetchCartItems().catch((error: any) => {
      setIsLoadingCartItems(false);
      setHttpError(error.message);
    });
  }, [authState, deleteBook, changeAmount]);

  const deleteFetchBook = async (id: number) => {
    deleteBookInCart(id, authState);
    setDeleteBook(true);
  };

  if (isLoadingCartItems) {
    return <SpinnerLoading />;
  }

  if (httpError) {
    return (
      <div
        className="modal fade show"
        style={{ display: "block" }}
        tabIndex={-1}
        aria-modal="true"
      >
        <div className="modal-dialog" style={{ textAlign: "center" }}>
          <div className="modal-content">
            <div className="modal-body">{httpError}</div>
            <div className="modal-footer">
              <button
                className="btn btn-primary"
                onClick={() => window.location.reload()}
              >
                확인
              </button>
            </div>
          </div>
        </div>
      </div>
    );
  }
  async function increaseBookAmount(id: number, amount: number) {
    await increaseAmount(id, authState, amount);
    setChangeAmount(true);
  }
  async function decreaseBookAmount(id: number, amount: number) {
    await decreaseAmount(id, authState, amount);
    setChangeAmount(true);
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
        {cartItems.length > 0 ? (
          <>
            {cartItems.map((cartItem) => (
              <div key={cartItem.cartItemId}>
                <div className="row mt-3 mb-2">
                  <div className="d-flex col-md-8">
                    <input
                      id="selectedBook"
                      type="checkbox"
                      onChange={() => handleCheckboxChange(cartItem.book.id)}
                      checked={selectedBooks.includes(cartItem.book.id)}
                    />
                    <div className="col col-md-3">
                      <img
                        src={cartItem.book?.img}
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
                            to={`/checkout/${cartItem.book.id}`}
                          >
                            {cartItem.book.title}
                          </Link>
                        </h4>
                        <p
                          className="card-text"
                          onClick={() => deleteFetchBook(cartItem.cartItemId)}
                        >
                          삭제
                        </p>
                      </div>
                      <div className="card-text">
                        <h5>{cartItem.book.author}</h5>
                      </div>

                      <div className="card-text">
                        <p>
                          {cartItem.book.publisher} .
                          {cartItem.book.publicationDate}
                        </p>
                      </div>
                      <div className="cart-text input-group">
                        <div className="input-group-prepend">
                          <button
                            className="btn btn-warning"
                            type="button"
                            onClick={() =>
                              decreaseBookAmount(
                                cartItem.cartItemId,
                                cartItem.amount
                              )
                            }
                          >
                            <Remove />
                          </button>
                        </div>
                        <div className="col-1">
                          <input
                            type="text"
                            className="form-control text-center"
                            value={cartItem.amount}
                            readOnly
                          />
                        </div>
                        <div className="input-group-append">
                          <button
                            className="btn btn-warning"
                            type="button"
                            onClick={() =>
                              increaseBookAmount(
                                cartItem.cartItemId,
                                cartItem.amount
                              )
                            }
                          >
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
                        <h4>₩ {cartItem.book.price * cartItem.amount}</h4>
                      </div>
                      <hr />
                      <Link
                        className="btn btn-primary"
                        to={`/checkout/${cartItem.book.id}`}
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
        {cartItems.length > 0 ? (
          <>
            {cartItems.map((cartItem) => (
              <div key={cartItem.cartItemId} style={{ textAlign: "center" }}>
                <input
                  id="selectedBook"
                  type="checkbox"
                  onChange={() => handleCheckboxChange(cartItem.book.id)}
                  checked={selectedBooks.includes(cartItem.book.id)}
                />
                <div className="d-flex justify-content-center align-items-center">
                  <img
                    src={cartItem.book?.img}
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
                        to={`/checkout/${cartItem.book.id}`}
                      >
                        {cartItem.book.title}
                      </Link>
                    </h4>
                    <p
                      className="card-text"
                      onClick={() => deleteFetchBook(cartItem.cartItemId)}
                    >
                      삭제
                      <Clear />
                    </p>
                  </div>
                  <div className="card-title">
                    <h5>{cartItem.book.author}</h5>
                  </div>
                  <div className="card-title">
                    <p>
                      {cartItem.book.publisher} .{" "}
                      {cartItem.book.publicationDate}
                    </p>
                  </div>
                </div>
                <div className="card d-flex mt-5 mb-3">
                  <div className="card-body container">
                    <div className="mt-3">
                      <h4>₩ {cartItem.book.price}</h4>
                    </div>
                    <hr />
                    <Link
                      className="btn btn-primary"
                      to={`/checkout/${cartItem.book.id}`}
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
