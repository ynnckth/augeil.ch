import React from "react";
import {
  Button,
  Dialog,
  DialogActions,
  DialogContent,
  DialogTitle,
  Typography,
} from "@mui/material";
import { Album } from "../../model/Album";
import AlbumDetailsGrid from "../AlbumDetailsTable/AlbumDetailsGrid";

interface Props {
  isOpen: boolean;
  onCancel: () => void;
  selectedAlbum?: Album;
}

const AlbumDetailsDialog: React.FC<Props> = ({
  isOpen,
  onCancel,
  selectedAlbum,
}) => {
  return (
    <Dialog open={isOpen} onClose={() => onCancel()}>
      <DialogTitle>Album Details</DialogTitle>
      <DialogContent>
        <Typography>Artist: {selectedAlbum?.artist}</Typography>
        <Typography>Album: {selectedAlbum?.albumName}</Typography>
        <br />
        <Typography>Download codes:</Typography>
        {selectedAlbum && <AlbumDetailsGrid album={selectedAlbum} />}
      </DialogContent>
      <DialogActions>
        <Button onClick={() => onCancel()}>Cancel</Button>
      </DialogActions>
    </Dialog>
  );
};
export default AlbumDetailsDialog;
