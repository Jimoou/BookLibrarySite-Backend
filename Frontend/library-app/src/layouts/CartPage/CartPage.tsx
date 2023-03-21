import { useOktaAuth } from "@okta/okta-react";
import { useNavigate } from "react-router-dom";
import { CartItem } from "./components/CartItem";
import { PaymentHistoryPage } from "./components/PaymentHistoryPage";

export const CartPage = () => {
  const navigate = useNavigate();
  const { authState } = useOktaAuth();

  if (!authState?.isAuthenticated) {
    navigate("/login");
  }

  return (
    <div className="container">
      <div className="mt-3">
        <nav>
          <div className="nav nav-tabs" id="nav-tab" role="tablist">
            <button
              className="nav-link active"
              id="nav-loans-tab"
              data-bs-toggle="tab"
              data-bs-target="#nav-loans"
              type="button"
              role="tab"
              aria-controls="nav-loans"
              aria-selected="true"
            >
              내 장바구니
            </button>
            <button
              className="nav-link"
              id="nav-history-tab"
              data-bs-toggle="tab"
              data-bs-target="#nav-history"
              type="button"
              role="tab"
              aria-controls="nav-history"
              aria-selected="false"
            >
              구매 내역
            </button>
          </div>
        </nav>
        <div className="tab-content" id="nav-tabContent">
          <div
            className="tab-pane fade show active"
            id="nav-loans"
            role="tabpanel"
            aria-labelledby="nav-loans-tab"
          >
            <CartItem />
          </div>
          <div
            className="tab-pane fade"
            id="nav-history"
            role="tabpanel"
            aria-labelledby="nav-history-tab"
          >
            <PaymentHistoryPage />
          </div>
        </div>
      </div>
    </div>
  );
};
