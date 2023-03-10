import { Link } from "react-router-dom";
import "./ExploreTopBooks.css";
export const ExploreTopBooks = () => {
  return (
    <div id="explore-top-books-container">
      <div id="hopeful-message">
        <h1>책이 주는 즐거움을 느껴보세요!</h1>
        <p>지금 바로 도서관에서 좋은 책을 찾아보세요.</p>
        <Link id="search-link" to="/search">
          책 찾아보기
        </Link>
      </div>
      <div className="carousel slide">
        <div className="carousel-inner">
          <div className="carousel-item active">
            <img
              src="image-5.jpg"
              className="d-block"
              alt="..."
              style={{ width: "100%", height: "400px", overflow: "hidden" }}
            />
          </div>
          <div className="carousel-item">
            <img
              src="image-2.jpg"
              className="d-block"
              alt="..."
              style={{ width: "100%", height: "400px", overflow: "hidden" }}
            />
          </div>
          <div className="carousel-item">
            <img
              src="image-1.jpg"
              className="d-block"
              alt="..."
              style={{ width: "100%", height: "400px", overflow: "hidden" }}
            />
          </div>
        </div>
        <button
          className="carousel-control-prev"
          type="button"
          data-bs-target="#carouselExample"
          data-bs-slide="prev"
        >
          <span
            className="carousel-control-prev-icon"
            aria-hidden="true"
          ></span>
          <span className="visually-hidden">Previous</span>
        </button>
        <button
          className="carousel-control-next"
          type="button"
          data-bs-target="#carouselExample"
          data-bs-slide="next"
        >
          <span
            className="carousel-control-next-icon"
            aria-hidden="true"
          ></span>
          <span className="visually-hidden">Next</span>
        </button>
      </div>
    </div>
  );
};
