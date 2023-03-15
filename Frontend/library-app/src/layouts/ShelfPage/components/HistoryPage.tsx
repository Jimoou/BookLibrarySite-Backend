import { useOktaAuth } from "@okta/okta-react";
import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import CheckoutHistoryModel from "../../../models/CheckoutHistoryModel";
import { Pagination } from "../../Utils/Pagination";
import { SpinnerLoading } from "../../Utils/SpinnerLoading";

export const HistoryPage = () => {
  const { authState } = useOktaAuth();
  const [isLoadingHistory, setIsLoadingHistory] = useState(true);
  const [httpError, setHttpError] = useState(null);

  // Histories
  const [histories, setHistories] = useState<CheckoutHistoryModel[]>([]);

  // Pagination
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(0);

  useEffect(() => {
    const fetchUserCheckoutHistory = async () => {
      if (authState && authState.isAuthenticated) {
        const url = `${
          process.env.REACT_APP_API
        }/checkout-histories/search/findBooksByUserEmail/?userEmail=${
          authState.accessToken?.claims.sub
        }&page=${currentPage - 1}&size=5`;
        const requestOptions = {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
          },
        };
        const historyResponse = await fetch(url, requestOptions);
        if (!historyResponse.ok) {
          throw new Error("Something went wrong!");
        }
        const historyResponseJson = await historyResponse.json();

        setHistories(historyResponseJson._embedded.checkoutHistories);
        setTotalPages(historyResponseJson.page.totalPages);
      }
      setIsLoadingHistory(false);
    };
    fetchUserCheckoutHistory().catch((error: any) => {
      setIsLoadingHistory(false);
      setHttpError(error.message);
    });
  }, [authState, currentPage]);

  if (isLoadingHistory) {
    return <SpinnerLoading />;
  }

  if (httpError) {
    return (
      <div className="container m-5">
        <p>{httpError}</p>
      </div>
    );
  }

  const paginate = (pageNumber: number) => setCurrentPage(pageNumber);

  return (
    <div className="mt-2">
      {histories.length > 0 ? (
        <>
          {histories
            .sort((a, b) => b.returnedDate.localeCompare(a.returnedDate))
            .map((history) => (
              <div key={history.id}>
                <div className="card mt-3 shadow p-3 mb-3 bg-body rounded">
                  <div className="row g-0">
                    <div className="col-md-2">
                      <div className="d-none d-lg-block">
                        <img
                          src={history.img}
                          width="195"
                          height="268"
                          alt="Book"
                          className="shadow bg-body-tertiary"
                        />
                      </div>
                      <div className="d-lg-none d-flex justify-content-center align-items-center">
                        <img
                          src={history.img}
                          width="195"
                          height="268"
                          alt="Book"
                          className="shadow bg-body-tertiary"
                        />
                      </div>
                    </div>
                    <div className="col">
                      <div className="card-body">
                        <h4>{history.title}</h4>
                        <h5 className="card-title"> {history.author} </h5>
                        <p className="card-text">{history.description}</p>
                        <hr />
                        <p className="card-text">
                          {" "}
                          대여일 : {history.checkoutDate}
                        </p>
                        <p className="card-text">
                          {" "}
                          반납일 : {history.returnedDate}
                        </p>
                      </div>
                    </div>
                  </div>
                </div>
                <hr />
              </div>
            ))}
        </>
      ) : (
        <>
          <h3 className="mt-3">내역이 없습니다. </h3>
          <Link className="btn btn-primary" to={"search"}>
            책 찾아보기
          </Link>
        </>
      )}
      {totalPages > 1 && (
        <Pagination
          currentPage={currentPage}
          totalPages={totalPages}
          paginate={paginate}
        />
      )}
    </div>
  );
};
