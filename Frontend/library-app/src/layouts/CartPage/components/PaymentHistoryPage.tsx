import { useOktaAuth } from "@okta/okta-react";
import { useEffect, useState } from "react";
import PaymentHistory from "../../../models/PaymentHistory";

export const PaymentHistoryPage = () => {
  const { authState } = useOktaAuth();
  const [httpError, setHttpError] = useState("");

  const [paymentHistories, setPaymentHistories] = useState<PaymentHistory[]>(
    []
  );

  useEffect(() => {
    const getPaymentHistory = async () => {
      if (authState && authState.isAuthenticated) {
        const url = `${process.env.REACT_APP_API}/payment/secure/histories`;
        const requestOptions = {
          method: "GET",
          headers: {
            Authorization: `Bearer ${authState.accessToken?.accessToken}`,
            "Content-Type": "application/json",
          },
        };
        const response = await fetch(url, requestOptions);
        const responseJson = await response.json();
        setPaymentHistories(responseJson);
        if (!response.ok) {
          throw new Error("Something went wrong!");
        }
      }
    };

    getPaymentHistory().catch((error: any) => {
      setHttpError(error.message);
    });
  }, [authState]);

  return (
    <>
      {paymentHistories.length > 0 ? (
        paymentHistories.map((paymentHistory) => (
          <ul key={paymentHistory.paymentKey}>
            <li>{paymentHistory.orderName}</li>
            <li>{paymentHistory.totalPrice}</li>
            <li>{paymentHistory.paymentDate}</li>
            <li>{paymentHistory.orderId}</li>
          </ul>
        ))
      ) : (
        <div>
          <p>구매 내역이 없습니다.</p>
        </div>
      )}
    </>
  );
};
