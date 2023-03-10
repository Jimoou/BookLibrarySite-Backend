import { Add, Remove } from "@mui/icons-material";
import { useOktaAuth } from "@okta/okta-react";
import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import BookModel from "../../../models/BookModel";

export const SearchBook: React.FC<{ book: BookModel }> = (props) => {
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
  }, []);

  return (
    <div className="card mt-3 shadow p-3 mb-3 bg-body rounded">
      <div className="row g-0">
        <div
          className="col-md-2"
          style={{
            display: "flex",
            flexDirection: "column",
            justifyContent: "center",
          }}
        >
          <div className="d-none d-lg-block" style={{ textAlign: "center" }}>
            <Link to={`/checkout/${props.book.id}`}>
              <img
                src={props.book.img}
                width="195"
                height="268"
                alt="Book"
                className="shadow bg-body-tertiary"
              />
            </Link>
          </div>
          <div
            className="d-lg-none d-flex justify-content-center 
                        align-items-center"
          >
            <Link to={`/checkout/${props.book.id}`}>
              <img
                src={props.book.img}
                width="195"
                height="268"
                alt="Book"
                className="shadow bg-body-tertiary"
              />
            </Link>
          </div>
        </div>
        <div className="col-md-6">
          <div
            className="card-body"
            style={{
              whiteSpace: "normal",
              overflow: "hidden",
              height: "350px",
              textOverflow: "ellipsis",
            }}
          >
            <Link
              to={`/checkout/${props.book.id}`}
              style={{ color: "black", textDecoration: "none" }}
            >
              <h4>{props.book.title}</h4>
            </Link>
            <h5 className="card-title">{props.book.author}</h5>
            <p className="card-text">{props.book.description}</p>
          </div>
        </div>
        <div
          className="col-md-4 d-flex justify-content-center align-items-center"
          style={{ flexDirection: "column" }}
        >
          {authState?.isAuthenticated &&
          authState.accessToken?.claims?.userType === "admin" ? (
            <>
              <p>
                총 수량 : <b>{quantity}</b>
              </p>
              <p>
                남은 책 : <b>{remaining}</b>
              </p>
              <button className="btn mt-1 btn-danger">삭제</button>
              <button className="btn mt-1 btn-primary main-color text-white">
                수량 <Add />
              </button>
              <button className="btn btn-warning">
                수량 <Remove />
              </button>
            </>
          ) : (
            <Link
              className="btn btn-secondary text-white"
              to={`/checkout/${props.book.id}`}
            >
              자세히 보기
            </Link>
          )}
        </div>
      </div>
    </div>
  );
};
