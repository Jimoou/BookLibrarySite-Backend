import { useOktaAuth } from "@okta/okta-react";
import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import BookModel from "../../models/BookModel";
import { LeaveAReview } from "../Utils/LeaveAReview";
import {
  Button,
  Card,
  CardContent,
  Typography,
  Grid,
  Dialog,
  DialogActions,
  DialogContent,
  DialogContentText,
} from "@mui/material";

export const CheckOutAndReviewBox: React.FC<{
  book: BookModel | undefined;
  mobile: boolean;
  currentLoansCount: number;
  isAuthenticated: any;
  isCheckedOut: boolean;
  checkoutBook: any;
  isReviewLeft: boolean;
  submitReview: any;
  addBookInCart: any;
}> = (props) => {
  const [isOpen, setIsOpen] = useState(false);
  const navigate = useNavigate();
  const { authState } = useOktaAuth();
  const confirm = () => {
    setIsOpen(false);
    navigate("/cart");
  };

  const cancel = () => {
    setIsOpen(false);
  };

  const handleClick = () => {
    props.addBookInCart();
    setIsOpen(true);
  };

  const renderModal = () => {
    return (
      <Dialog open={isOpen} onClose={cancel}>
        <DialogContent>
          <DialogContentText>
            장바구니에 추가되었습니다. 장바구니로 바로 이동하시겠습니까?
          </DialogContentText>
        </DialogContent>
        <DialogActions>
          <Button onClick={cancel} color="secondary">
            취소
          </Button>
          <Button onClick={confirm} color="primary">
            이동
          </Button>
        </DialogActions>
      </Dialog>
    );
  };

  const buttonRender = () => {
    if (props.isAuthenticated) {
      if (!props.isCheckedOut && props.currentLoansCount < 5) {
        return (
          <Button
            onClick={() => props.checkoutBook()}
            variant="contained"
            color="success"
          >
            대여하기
          </Button>
        );
      } else if (props.isCheckedOut) {
        return (
          <Typography variant="body1">
            <b>책을 대여했습니다.</b>
          </Typography>
        );
      } else if (!props.isCheckedOut) {
        return (
          <Typography variant="body1" color="error">
            더이상 대여할 수 없습니다.
          </Typography>
        );
      }
    }
    return (
      <Link to={"/login"} style={{ textDecoration: "none" }}>
        <Button variant="contained" color="success" size="large">
          로그인
        </Button>
      </Link>
    );
  };

  const reviewRender = () => {
    if (props.isAuthenticated && !props.isReviewLeft) {
      return <LeaveAReview submitReview={props.submitReview} />;
    } else if (props.isAuthenticated && props.isReviewLeft) {
      return (
        <Typography variant="body1">
          <b>리뷰를 남긴 도서입니다.</b>
        </Typography>
      );
    }
    return (
      <div>
        <hr />
        <Typography variant="body1">로그인 후 리뷰를 남겨보세요!</Typography>
      </div>
    );
  };

  return (
    <Card
      sx={{
        mt: props.mobile ? 5 : 1,
        mb: props.mobile ? 0 : 5,
        maxWidth: 350,
        maxHeight: 720,
      }}
    >
      <CardContent>
        <Typography variant="body1">
          <b>{props.currentLoansCount}/5</b> 나의 대여 권 수
        </Typography>
        <hr />
        {props.book &&
        props.book.copiesAvailable &&
        props.book.copiesAvailable > 0 ? (
          <Typography variant="h4" color="success">
            대여 가능함
          </Typography>
        ) : (
          <Typography variant="h4" color="error">
            대여 불가능
          </Typography>
        )}
        <Grid container spacing={2}>
          <Grid item xs={6}>
            <Typography variant="h6">
              <b>대여 권 수 : {props.book?.copies}</b>
            </Typography>
          </Grid>
          <Grid item xs={6}>
            <Typography variant="h6">
              <b>남은 권 수 : {props.book?.copiesAvailable}</b>
            </Typography>
          </Grid>
        </Grid>
        {buttonRender()}
        <hr />
        <Typography variant="h4" color="success">
          구매하기
        </Typography>
        <Grid container spacing={2}>
          <Grid item xs={8}>
            <Typography variant="h6">
              <b>가격 : {props.book?.price} 원</b>
            </Typography>
          </Grid>
        </Grid>

        {authState?.isAuthenticated && (
          <>
            <Button variant="contained" color="info">
              바로구매
            </Button>
            <Button
              variant="contained"
              color="warning"
              sx={{ m: 1 }}
              onClick={handleClick}
            >
              장바구니
            </Button>
          </>
        )}

        {isOpen && renderModal()}
        <hr />
        <Typography variant="body1" sx={{ mt: 3 }}>
          책의 수량은 변경될 수 있습니다.
        </Typography>
        {reviewRender()}
      </CardContent>
    </Card>
  );
};
