/* eslint-disable react-hooks/exhaustive-deps */
import { useOktaAuth } from "@okta/okta-react";
import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { AddNewBook } from "./components/AddNewBook";
import { AdminMessages } from "./components/AdminMessages";
import { ChangeQuantityOfBooks } from "./components/ChangeQuantityOfBooks";

export const ManageLibraryPage = () => {
  const { authState, oktaAuth } = useOktaAuth();
  const navigate = useNavigate();
  useEffect(() => {
    async function authenticate() {
      if (!authState) return;

      if (
        !authState.isAuthenticated ||
        authState.accessToken?.claims?.userType !== "admin"
      ) {
        navigate("/");
      }
    }
    authenticate();
  }, [authState, oktaAuth]);

  const [changeQuantityOfBooksClick, setChangeQuantityOfBooksClick] =
    useState(false);
  const [messagesClick, setMessagesClick] = useState(false);

  function addBookClickFunction() {
    setChangeQuantityOfBooksClick(false);
    setMessagesClick(false);
  }

  function changeQuantityOfBooksClickFunction() {
    setChangeQuantityOfBooksClick(true);
    setMessagesClick(false);
  }

  function messagesClickFunction() {
    setChangeQuantityOfBooksClick(false);
    setMessagesClick(true);
  }

  if (authState?.accessToken?.claims.userType === undefined) {
    navigate("/home");
  }
  if (
    !authState?.isAuthenticated ||
    authState?.accessToken?.claims?.userType !== "admin"
  ) {
    return (
      <>
        <div>잘못된 접근입니다.</div>
      </>
    );
  }
  return (
    <div className="container">
      <div className="mt-5">
        <h3>관리자 페이지</h3>
        <nav>
          <div className="nav nav-tabs" id="nav-tab" role="tablist">
            <button
              onClick={addBookClickFunction}
              className="nav-link active"
              id="nav-add-book-tab"
              data-bs-toggle="tab"
              data-bs-target="#nav-add-book"
              type="button"
              role="tab"
              aria-controls="nav-add-book"
              aria-selected="false"
            >
              책 추가하기
            </button>
            <button
              onClick={changeQuantityOfBooksClickFunction}
              className="nav-link"
              id="nav-quantity-tab"
              data-bs-toggle="tab"
              data-bs-target="#nav-quantity"
              type="button"
              role="tab"
              aria-controls="nav-quantity"
              aria-selected="true"
            >
              도서 관리
            </button>
            <button
              onClick={messagesClickFunction}
              className="nav-link"
              id="nav-messages-tab"
              data-bs-toggle="tab"
              data-bs-target="#nav-messages"
              type="button"
              role="tab"
              aria-controls="nav-messages"
              aria-selected="false"
            >
              문의 답변
            </button>
          </div>
        </nav>
        <div className="tab-content" id="nav-tabContent">
          <div
            className="tab-pane fade show active"
            id="nav-add-book"
            role="tabpanel"
            aria-labelledby="nav-add-book-tab"
          >
            <AddNewBook />
          </div>
          <div
            className="tab-pane fade"
            id="nav-quantity"
            role="tabpanel"
            aria-labelledby="nav-quantity-tab"
          >
            {changeQuantityOfBooksClick ? <ChangeQuantityOfBooks /> : <></>}
          </div>
          <div
            className="tab-pane fade"
            id="nav-messages"
            role="tabpanel"
            aria-labelledby="nav-messages-tab"
          >
            {messagesClick ? <AdminMessages /> : <></>}
          </div>
        </div>
      </div>
    </div>
  );
};
