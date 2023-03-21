import React from "react";
import { Navbar } from "./layouts/NavbarAndFooter/Navbar";
import { HomePage } from "./layouts/HomePage/HomePage";
import { SearchBooksPage } from "./layouts/SearchBooksPage/SearchBooksPage";
import { Navigate, Route, Routes, useNavigate } from "react-router-dom";
import { BookCheckoutPage } from "./layouts/BookCheckoutPage/BookCheckoutPage";
import { oktaConfig } from "./lib/oktaConfig";
import { OktaAuth, toRelativeUrl } from "@okta/okta-auth-js";
import { LoginCallback, Security } from "@okta/okta-react";
import LoginWidget from "./Auth/LoginWidget";
import { ReviewListPage } from "./layouts/BookCheckoutPage/ReviewListPage/ReviewListPage";
import { ShelfPage } from "./layouts/ShelfPage/ShelfPage";
import { MessagesPage } from "./layouts/MessagesPage/MessagesPage";
import { ManageLibraryPage } from "./layouts/ManageLibraryPage/ManageLibraryPage";
import { CartPage } from "./layouts/CartPage/CartPage";
import RegisterWidget from "./Auth/RegisterWidget";
import { Success } from "./layouts/CartPage/components/Success";
import { SuccessPage } from "./layouts/CartPage/components/SuccessPage";
import { PaymentHistoryPage } from "./layouts/CartPage/components/PaymentHistoryPage";

function App() {
  const oktaAuth = new OktaAuth(oktaConfig);
  const navigate = useNavigate();
  const customAuthHandler = () => {
    navigate("/login");
  };

  const restoreOriginalUri = async (
    _oktaAuth: any,
    originalUri: string | undefined
  ) => {
    const origin = window.location.origin;
    navigate(originalUri ? toRelativeUrl(originalUri, origin) : "/", {
      replace: true,
    });
  };

  return (
    <div className="d-flex flex-column min-vh-100">
      <Security
        oktaAuth={oktaAuth}
        restoreOriginalUri={restoreOriginalUri}
        onAuthRequired={customAuthHandler}
      >
        <Navbar />
        <div className="flex-grow-1">
          <Routes>
            <Route path="/home" element={<HomePage />} />
            <Route path="/" element={<Navigate replace to="/home" />} />
            <Route path="/search" element={<SearchBooksPage />} />
            <Route path="/reviewlist/:bookId" element={<ReviewListPage />} />
            <Route path="/checkout/:bookId" element={<BookCheckoutPage />} />
            <Route
              path="/login"
              element={<LoginWidget config={oktaConfig} />}
            />
            <Route path="/register" element={<RegisterWidget />} />
            <Route path="/login/callback" element={<LoginCallback />} />
            <Route path="/shelf" element={<ShelfPage />} />
            <Route path="/messages" element={<MessagesPage />} />
            <Route path="/admin" element={<ManageLibraryPage />} />
            <Route path="/cart" element={<CartPage />} />
            <Route path="/success" element={<Success />} />
            <Route path="/success-complete" element={<SuccessPage />} />
            <Route path="/payment-histories" element={<PaymentHistoryPage />} />
          </Routes>
        </div>
      </Security>
    </div>
  );
}

export default App;
