import { useOktaAuth } from "@okta/okta-react";
import { useEffect } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import SuccessPaymentRequest from "../../../models/SuccessPaymentRequest";
import { SpinnerLoading } from "../../Utils/SpinnerLoading";

export const Success = () => {
  const { authState } = useOktaAuth();
  const isLoading = true;

  const location = useLocation();
  const searchParams = new URLSearchParams(location.search);
  const paymentKey = String(searchParams.get("paymentKey"));
  const orderId = String(searchParams.get("orderId"));
  const amount = Number(searchParams.get("amount"));
  const successPaymentRequest = new SuccessPaymentRequest(
    paymentKey,
    orderId,
    amount
  );

  const navigate = useNavigate();

  useEffect(() => {
    const fetchData = async () => {
      try {
        await confirmPayments();
        navigate("/success-complete");
      } catch (error: any) {
        console.log(error.message);
      }
    };

    if (authState && authState.isAuthenticated) {
      fetchData();
    }
  }, [authState]);

  const confirmPayments = async () => {
    if (authState && authState.isAuthenticated) {
      const url = `${process.env.REACT_APP_API}/payment/secure/confirm`;
      const requestOptions = {
        method: "POST",
        headers: {
          Authorization: `Bearer ${authState.accessToken?.accessToken}`,
          "Content-Type": "application/json",
        },
        body: JSON.stringify(successPaymentRequest),
      };
      await fetch(url, requestOptions)
        .then((response) => {
          console.log(response.status);
        })
        .catch((error) => {
          console.error("Error:", error);
        });
    }
  };

  return (
    <div className="container mt-5 mb-5">
      <h1>결제중입니다.</h1>
      <h2>{isLoading && <SpinnerLoading />}</h2>
    </div>
  );
};
