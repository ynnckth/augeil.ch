import React from "react";
import { Box, CircularProgress, Typography } from "@mui/material";

interface Props {
  loadingText?: string;
}

const LoadingScreen: React.FC<Props> = ({ loadingText }) => {
  return (
    <Box
      sx={{
        position: "absolute",
        top: 0,
        left: 0,
        width: "100vw",
        height: "100vh",
        backgroundColor: "black",
        opacity: "0.7",
        display: "flex",
        alignItems: "center",
        justifyContent: "center",
      }}
    >
      <CircularProgress />
      {loadingText && <Typography>{loadingText}</Typography>}
    </Box>
  );
};
export default LoadingScreen;
