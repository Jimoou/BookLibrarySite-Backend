import { Carousel } from "./Components/Carouesl";
import { ExploreTopBooks } from "./Components/ExploreTopBooks";
import { Heros } from "./Components/Heros";
import { LibraryServices } from "./Components/LibraryServices";

export const HomePage = () => {
  return (
    <>
      <ExploreTopBooks />
      <Carousel />
      <Heros />
      <LibraryServices />
    </>
  );
};
