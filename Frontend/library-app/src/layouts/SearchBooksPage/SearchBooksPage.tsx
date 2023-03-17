import { useEffect, useState } from "react";
import BookModel from "../../models/BookModel";
import { Pagination, PaginationItem } from "@mui/material";
import { SpinnerLoading } from "../Utils/SpinnerLoading";
import { SearchBook } from "./components/SearchBook";
import SearchIcon from "@mui/icons-material/Search";
import CategoryList from "../ManageLibraryPage/components/CategoryList";
import {
  Box,
  Button,
  Container,
  Grid,
  InputAdornment,
  TextField,
  Typography,
} from "@mui/material";

export const SearchBooksPage = () => {
  // Categories
  const categories = [
    { label: "모든 분야", value: "모든 분야" },
    { label: "컴퓨터/IT", value: "컴퓨터/IT" },
    { label: "자기계발", value: "자기계발" },
    { label: "만화", value: "만화" },
    { label: "경제/경영", value: "경제/경영" },
    { label: "소설", value: "소설" },
    { label: "정치/사회", value: "정치/사회" },
    { label: "인문", value: "인문" },
    { label: "종교", value: "종교" },
    { label: "시/에세이", value: "시/에세이" },
  ];

  const [books, setBooks] = useState<BookModel[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [httpError, setHttpError] = useState(null);
  const [currentPage, setCurrentPage] = useState(1);
  const [booksPerPage] = useState(5);
  const [totalAmountOfBooks, setTotalAmountOfBooks] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [search, setSearch] = useState("");
  const [searchUrl, setSearchUrl] = useState("");
  const [categorySelection, setCategorySelection] = useState("모든 분야");
  const [bookDelete, setBookDelete] = useState(false);

  useEffect(() => {
    const fetchBooks = async () => {
      const baseUrl: string = `${process.env.REACT_APP_API}/books`;

      let url: string = "";

      if (searchUrl === "") {
        url = `${baseUrl}?page=${currentPage - 1}&size=${booksPerPage}`;
      } else {
        url = baseUrl + searchUrl;
      }

      const response = await fetch(url);

      if (!response.ok) {
        throw new Error("Something went wrong!");
      }

      const responseJson = await response.json();

      const responseData = responseJson._embedded.books;

      setTotalAmountOfBooks(responseJson.page.totalElements);
      setTotalPages(responseJson.page.totalPages);

      const loadedBooks: BookModel[] = [];

      for (const key in responseData) {
        loadedBooks.push({
          id: responseData[key].id,
          title: responseData[key].title,
          author: responseData[key].author,
          description: responseData[key].description,
          copies: responseData[key].copies,
          copiesAvailable: responseData[key].copiesAvailable,
          category: responseData[key].category,
          img: responseData[key].img,
          publisher: responseData[key].publisher,
          price: responseData[key].price,
          coin: responseData[key].coin,
          publicationDate: responseData[key].publicationDate,
        });
      }
      setBooks(loadedBooks);
      setIsLoading(false);
    };
    fetchBooks().catch((error: any) => {
      setIsLoading(false);
      setHttpError(error.message);
    });
    window.scrollTo(0, 0);
  }, [currentPage, searchUrl, bookDelete]);

  if (isLoading) {
    return <SpinnerLoading />;
  }

  if (httpError) {
    return (
      <div className="container m-5">
        <p>{httpError}</p>
      </div>
    );
  }

  const searchHandleChange = () => {
    if (search === "") {
      setSearchUrl("");
    } else {
      setSearchUrl(
        `/search/findByTitleContaining?title=${search}&page=0&size=${booksPerPage}`
      );
    }
  };

  const categoryField = (value: string) => {
    if (
      value === "컴퓨터/IT" ||
      value === "자기계발" ||
      value === "만화" ||
      value === "경제/경영" ||
      value === "소설" ||
      value === "정치/사회" ||
      value === "인문" ||
      value === "종교" ||
      value === "시/에세이"
    ) {
      setCategorySelection(value);
      setSearchUrl(
        `/search/findByCategory?category=${value}&page=0&size=${booksPerPage}`
      );
    } else {
      setCategorySelection("모든 분야");
      setSearchUrl("");
    }
  };

  const paginate = (pageNumber: number) => setCurrentPage(pageNumber);
  const deleteBook = () => setBookDelete(!bookDelete);

  return (
    <Box>
      <Container>
        <Grid container spacing={3} mt={5}>
          <Grid item xs={12} md={6}>
            <TextField
              fullWidth
              label="책 검색하기..."
              onChange={(e) => setSearch(e.target.value)}
              InputProps={{
                endAdornment: (
                  <InputAdornment position="end">
                    <Button onClick={() => searchHandleChange()}>
                      <SearchIcon />
                    </Button>
                  </InputAdornment>
                ),
              }}
            />
          </Grid>
          <Grid item xs={10} md={2} sx={{ my: 1 }}>
            <CategoryList categories={categories} onSelect={categoryField} />
          </Grid>
        </Grid>
        {totalAmountOfBooks > 0 ? (
          <>
            <Box mt={3}>
              <Typography variant="h5">
                모든 책 수: {totalAmountOfBooks} 권
              </Typography>
            </Box>
            {books.map((book) => (
              <SearchBook book={book} key={book.id} deleteBook={deleteBook} />
            ))}
          </>
        ) : (
          <Box m={5}>
            <Typography variant="h3">찾는 책이 없으신가요?</Typography>
            <Button
              variant="contained"
              color="primary"
              size="medium"
              sx={{ mt: 2 }}
              href="/messages"
            >
              문의 하기
            </Button>
          </Box>
        )}
        {totalPages > 1 && (
          <Box mt={3}>
            <Pagination
              count={totalPages}
              onChange={(event, value) => paginate(value)}
              page={currentPage}
              boundaryCount={1}
              siblingCount={0}
              size="large"
              sx={{ my: 4 }}
              renderItem={(item) => (
                <PaginationItem {...item} component="button" />
              )}
            />
          </Box>
        )}
      </Container>
    </Box>
  );
};
