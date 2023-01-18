import React from "react";
import { Footer } from "./layouts/NavbarAndFooter/Footer";
import { Navbar } from "./layouts/NavbarAndFooter/Navbar";
import { HomPage } from "./layouts/HomePage/HomePage";
import { SearchBooksPage } from "./layouts/SearchBooksPage/SearchBooksPage";

function App() {
  return (
    <>
      <Navbar />
        {/* <HomPage /> */}
        <SearchBooksPage />
      <Footer />
      </>
  );
}

export default App;
