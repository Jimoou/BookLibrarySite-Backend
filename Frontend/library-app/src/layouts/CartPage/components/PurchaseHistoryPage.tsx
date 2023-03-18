import { useOktaAuth } from "@okta/okta-react";
import { useEffect, useState } from "react";
import PurchaseHistory from "../../../models/PurchaseHistory";

export const PurchaseHistoryPage = () => {
  const { authState } = useOktaAuth();
  const [httpError, setHttpError] = useState("");

  const [purchaseHistories, setPurchaseHistories] = useState<PurchaseHistory[]>(
    []
  );

  useEffect(() => {
    const getPurchaseHistory = async () => {
      if (authState && authState.isAuthenticated) {
        const url = `${process.env.REACT_APP_API}/purchase/secure/histories`;
        const requestOptions = {
          method: "GET",
          headers: {
            Authorization: `Bearer ${authState.accessToken?.accessToken}`,
            "Content-Type": "application/json",
          },
        };
        const response = await fetch(url, requestOptions);
        const responseJson = await response.json();
        setPurchaseHistories(responseJson);
        if (!response.ok) {
          throw new Error("Something went wrong!");
        }
      }
    };

    getPurchaseHistory().catch((error: any) => {
      setHttpError(error.message);
    });
  }, [authState]);

  return (
    <>
      {purchaseHistories.length > 0 ? (
        purchaseHistories.map((purchaseHistory) => (
          <ul key={purchaseHistory.paymentKey}>
            <li>{purchaseHistory.orderName}</li>
            <li>{purchaseHistory.totalPrice}</li>
            <li>{purchaseHistory.purchaseDate}</li>
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
