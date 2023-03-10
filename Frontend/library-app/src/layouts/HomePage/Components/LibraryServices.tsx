import { useOktaAuth } from "@okta/okta-react";
import { Link } from "react-router-dom";

export const LibraryServices = () => {
  const { authState } = useOktaAuth();

  return (
    <div className="container my-5">
      <div className="row p-4 align-items-center border shadow-lg">
        <div className="col-lg-7 p-3">
          <h1 className="display-4 fw-bold">찾는 책이 없으신가요?</h1>
          <p className="lead">
            찾는 책이 없으시다면, 원하시는 책을 저희에게 알려주세요.
          </p>
          <div className="d-grid gap-2 justify-content-md-start mb-4 mb-lg-3">
            {authState?.isAuthenticated ? (
              <Link
                to="/messages"
                type="button"
                className="btn btn-outline-secondary btn-lg px-4 me-md-2text-gray"
              >
                문의하기
              </Link>
            ) : (
              <Link
                type="button"
                className="btn btn-outline-secondary btn-lg px-4 me-md-2text-gray"
                to="/login"
              >
                로그인
              </Link>
            )}
          </div>
        </div>
        <div className="col-lg-4 offset-lg-1 shadow-lg lost-image"></div>
      </div>
    </div>
  );
};
