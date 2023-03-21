import { useOktaAuth } from "@okta/okta-react";
import { useEffect, useState } from "react";
import PaymentHistory from "../../../models/PaymentHistory";
import {
  Card,
  CardContent,
  Typography,
  Grid,
  Box,
  CardMedia,
} from "@mui/material";
import { Link } from "react-router-dom";

export const PaymentHistoryPage = () => {
  const { authState } = useOktaAuth();
  const [httpError, setHttpError] = useState("");

  const [paymentHistories, setPaymentHistories] = useState<
    Record<string, PaymentHistory[]>
  >({});

  useEffect(() => {
    const getPaymentHistory = async () => {
      if (authState && authState.isAuthenticated) {
        const url = `${process.env.REACT_APP_API}/payment/secure/history`;
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
    <Box mb={3}>
      {httpError && <div>{httpError}</div>}
      <Box>
        {Object.entries(paymentHistories).map(([date, paymentHistoryItems]) => (
          <Box key={date} mt={3}>
            <Typography variant="h5" component="h5" gutterBottom>
              {date.split("T")[0]}
            </Typography>
            {paymentHistoryItems.map((item) => (
              <Card
                key={item.orderId}
                sx={{ mt: 3, p: 3, boxShadow: 3, height: 200 }}
              >
                <Grid container flexDirection="row">
                  <Grid item xs={12} md={1} container alignItems="center">
                    <CardMedia
                      component="img"
                      image={item.img}
                      alt="Book"
                      sx={{
                        boxShadow: "0 3px 5px rgba(0, 0, 0, 0.3)",
                      }}
                    />
                  </Grid>
                  <Grid item xs={12} md={6} container ml={4}>
                    <CardContent
                      sx={{
                        whiteSpace: "normal",
                        overflow: "hidden",
                        maxHeight: "150px",
                        textOverflow: "ellipsis",
                      }}
                    >
                      <Typography
                        variant="h4"
                        gutterBottom
                        sx={{ fontSize: "1.2rem" }}
                      >
                        {item.title}
                      </Typography>
                      <Typography
                        variant="h5"
                        gutterBottom
                        sx={{ fontSize: "1rem" }}
                      >
                        {item.author}
                      </Typography>
                      <Typography
                        variant="body1"
                        gutterBottom
                        sx={{ fontSize: "0.8rem" }}
                      >
                        {item.publisher}
                      </Typography>
                    </CardContent>
                  </Grid>
                  <Grid
                    item
                    xs={12}
                    md={4}
                    container
                    justifyContent="flex-end"
                    alignItems="center"
                  >
                    <Grid>
                      <Typography variant="h6">수량 : {item.amount}</Typography>
                      <Typography variant="h6">
                        결제 금액 : {item.price}
                      </Typography>
                    </Grid>
                  </Grid>
                </Grid>
              </Card>
            ))}
          </Box>
        ))}
      </Box>
    </Box>
  );
};
