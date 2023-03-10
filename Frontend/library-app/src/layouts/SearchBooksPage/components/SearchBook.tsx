import { Link } from "react-router-dom";
import BookModel from "../../../models/BookModel";

export const SearchBook: React.FC<{ book: BookModel }> = (props) => {
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
            <img
              src={props.book.img}
              width="195"
              height="268"
              alt="Book"
              className="shadow bg-body-tertiary"
            />
          </div>
          <div
            className="d-lg-none d-flex justify-content-center 
                        align-items-center"
          >
            <img
              src={props.book.img}
              width="195"
              height="268"
              alt="Book"
              className="shadow bg-body-tertiary"
            />
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
            <h4>{props.book.title}</h4>
            <h5 className="card-title">{props.book.author}</h5>
            <p className="card-text">{props.book.description}</p>
          </div>
        </div>
        <div className="col-md-4 d-flex justify-content-center align-items-center">
          <Link
            className="btn btn-md btn-secondary text-white"
            to={`/checkout/${props.book.id}`}
          >
            자세히 보기
          </Link>
        </div>
      </div>
    </div>
  );
};
