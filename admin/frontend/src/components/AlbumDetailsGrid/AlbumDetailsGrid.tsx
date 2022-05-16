import React, { useState } from "react";
import { Album } from "../../model/Album";
import { AgGridReact } from "ag-grid-react";

interface Props {
  album: Album;
}

// TODO: resizable columns
const AlbumDetailsGrid: React.FC<Props> = ({ album }) => {
  const [columnDefs, setColumnDefs] = useState([
    { field: "id", filter: true },
    { field: "availableDownloads", filter: true },
  ]);

  return (
    <div
      className="ag-theme-alpine-dark"
      style={{ width: "450px", height: "300px" }}
    >
      <AgGridReact
        rowData={album.downloadCodes}
        columnDefs={columnDefs}
        enableCellTextSelection={true}
      />
    </div>
  );
};
export default AlbumDetailsGrid;
