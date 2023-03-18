import { Link } from "react-router-dom";
import ReviewModel from "../../models/ReviewModel";
import { Review } from "../Utils/Review";
import { Typography, Button, Grid, Box } from "@mui/material";
export const LatestReviews: React.FC<{
  reviews: ReviewModel[];
  bookId: number | undefined;
  mobile: boolean;
}> = (props) => {
  return (
    <Grid container spacing={2} sx={{ mt: props.mobile ? 3 : 5 }}>
      <Grid item xs={props.mobile ? 12 : 2}>
        <Typography variant="h4">최근 리뷰 :</Typography>
      </Grid>
      <Grid item xs={props.mobile ? 12 : 10}>
        {props.reviews.length > 0 ? (
          <>
            {props.reviews.slice(0, 3).map((eachReview) => (
              <Review review={eachReview} key={eachReview.id} />
            ))}
            <Box sx={{ m: 3 }}>
              <Button
                component={Link}
                to={`/reviewlist/${props.bookId}`}
                variant="contained"
                color="primary"
              >
                모든 리뷰 보기
              </Button>
            </Box>
          </>
        ) : (
          <Box sx={{ m: 3, mb: 5 }}>
            <Typography variant="h6">
              리뷰가 없습니다. 첫 리뷰를 남겨보세요.
            </Typography>
          </Box>
        )}
      </Grid>
    </Grid>
  );
};
