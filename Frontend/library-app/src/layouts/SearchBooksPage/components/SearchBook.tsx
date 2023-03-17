import { Add, Bolt, Book, Remove, Sell } from "@mui/icons-material";
import {
  Box,
  Card,
  CardContent,
  CardMedia,
  Grid,
  Typography,
  Button,
} from "@mui/material";
import { useOktaAuth } from "@okta/okta-react";
import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import BookModel from "../../../models/BookModel";

export const SearchBook: React.FC<{ book: BookModel; deleteBook: any }> = (
  props
) => {
  const { authState } = useOktaAuth();
  const [quantity, setQuantity] = useState<number>(0);
  const [remaining, setRemaining] = useState<number>(0);

  useEffect(() => {
    const fetchBookInState = () => {
      props.book.copies ? setQuantity(props.book.copies) : setQuantity(0);
      props.book.copiesAvailable
        ? setRemaining(props.book.copiesAvailable)
        : setRemaining(0);
    };
    fetchBookInState();
  }, [props.book.copies, props.book.copiesAvailable]);

  async function increaseQuantity() {
    const url = `${process.env.REACT_APP_API}/admin/secure/increase/book/quantity/?bookId=${props.book?.id}`;
    const requestOptions = {
      method: "PUT",
      headers: {
        Authorization: `Bearer ${authState?.accessToken?.accessToken}`,
        "Content-Type": "application/json",
      },
    };

    const quantityUpdateResponse = await fetch(url, requestOptions);
    if (!quantityUpdateResponse.ok) {
      throw new Error("something went wrong!");
    }
    setQuantity(quantity + 1);
    setRemaining(remaining + 1);
  }

  async function decreaseQuantity() {
    const url = `${process.env.REACT_APP_API}/admin/secure/decrease/book/quantity/?bookId=${props.book?.id}`;
    const requestOptions = {
      method: "PUT",
      headers: {
        Authorization: `Bearer ${authState?.accessToken?.accessToken}`,
        "Content-Type": "application/json",
      },
    };

    const quantityUpdateResponse = await fetch(url, requestOptions);
    if (!quantityUpdateResponse.ok) {
      throw new Error("something went wrong!");
    }
    setQuantity(quantity - 1);
    setRemaining(remaining - 1);
  }

  async function deleteBook() {
    const url = `${process.env.REACT_APP_API}/admin/secure/delete/book/?bookId=${props.book?.id}`;
    const requestOptions = {
      method: "DELETE",
      headers: {
        Authorization: `Bearer ${authState?.accessToken?.accessToken}`,
        "Content-Type": "application/json",
      },
    };

    const updateResponse = await fetch(url, requestOptions);
    if (!updateResponse.ok) {
      throw new Error("something went wrong!");
    }
    props.deleteBook();
  }

  return (
    <Card sx={{ mt: 3, p: 3, boxShadow: 3 }}>
      <Grid container>
        <Grid item xs={12} md={2} container alignItems="center">
          <Link to={`/checkout/${props.book.id}`}>
            <CardMedia
              component="img"
              width="195"
              height="268"
              image={props.book.img}
              alt="Book"
              sx={{ boxShadow: "0 3px 5px rgba(0, 0, 0, 0.3)" }}
            />
          </Link>
        </Grid>
        <Grid item xs={12} md={6} container ml={4}>
          <CardContent
            style={{
              whiteSpace: "normal",
              overflow: "hidden",
              maxHeight: "300px",
              textOverflow: "ellipsis",
            }}
          >
            <Link
              to={`/checkout/${props.book.id}`}
              style={{ color: "black", textDecoration: "none" }}
            >
              <Typography variant="h4" gutterBottom>
                {props.book.title}
              </Typography>
            </Link>
            <Typography variant="h5" gutterBottom>
              {props.book.author}
            </Typography>
            <Typography variant="body1" gutterBottom>
              {props.book.publisher}
            </Typography>
            <Typography variant="body2" gutterBottom>
              {props.book.description}
            </Typography>
          </CardContent>
        </Grid>
        <Grid
          item
          xs={12}
          md={3}
          ml={3}
          container
          justifyContent="center"
          alignItems="center"
          flexDirection="column"
        >
          {authState?.isAuthenticated &&
          authState.accessToken?.claims?.userType === "admin" ? (
            <>
              <Typography>
                총 수량 : <b>{quantity}</b>
              </Typography>
              <Typography>
                남은 책 : <b>{remaining}</b>
              </Typography>
              <Button
                onClick={deleteBook}
                variant="contained"
                color="error"
                className="mt-2"
              >
                삭제
              </Button>
              <Button
                onClick={increaseQuantity}
                variant="contained"
                color="primary"
                className="mt-2"
              >
                수량 <Add />
              </Button>
              <Button
                onClick={decreaseQuantity}
                variant="contained"
                color="warning"
                className="mt-2"
              >
                수량 <Remove />
              </Button>
            </>
          ) : (
            <>
              <Box>
                <hr />
                <Typography variant="h5">
                  구매 <Sell /> : {props.book.price} 원
                </Typography>
                <Typography variant="h5">
                  대여 <Book /> : {props.book.coin}{" "}
                  <Bolt
                    style={{
                      color: "yellow",
                      textShadow: "1px 1px 2px rgba(0, 0, 0, 0.5)",
                    }}
                  />
                </Typography>
                <hr />
              </Box>
              <Link
                to={`/checkout/${props.book.id}`}
                style={{ textDecoration: "none" }}
              >
                <Button variant="contained" color="secondary">
                  자세히 보기
                </Button>
              </Link>
            </>
          )}
        </Grid>
      </Grid>
    </Card>
  );
};
