import { Carousel } from "./Components/Carouesl";
import { ExploreTopBooks } from "./Components/ExploreTopBooks";
import { LibraryServices } from "./Components/LibraryServices";

export const HomePage = () => {
  return (
    <>
      <ExploreTopBooks />
      <Carousel />
      <LibraryServices />
    </>
  );
};
