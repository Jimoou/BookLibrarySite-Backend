import React from "react";
import { Link } from "react-router-dom";
import BookModel from "../../../models/BookModel";

export const ReturnBook: React.FC<{ book: BookModel }> = (props) => {
  return (
    <div className="col-xs-6 col-sm-6 col-md-4 col-lg-3 mb-3">
      <div className="text-center">
        <img
          src={props.book.img}
          width="151"
          height="233"
          alt="book"
          className="shadow bg-body-tertiary"
        />

        <h6 className="mt-3">{props.book.title}</h6>
        <p>{props.book.author}</p>
        <Link
          className="btn btn-primary text-white"
          to={`/checkout/${props.book.id}`}
        >
          예약하기
        </Link>
      </div>
    </div>
  );
};
