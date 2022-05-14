import React, { useState } from "react";
import { Album } from "../../model/Album";
import { AgGridReact } from "ag-grid-react";

import "ag-grid-community/dist/styles/ag-grid.css";
import "ag-grid-community/dist/styles/ag-theme-alpine-dark.css";

interface Props {
  albums: Album[];
}

const AlbumGrid: React.FC<Props> = ({ albums }) => {
  const [columnDefs, setColumnDefs] = useState([
    { field: "id", filter: true },
    { field: "artist", filter: true },
    { field: "albumName", filter: true },
  ]);

  return (
    <div
      className="ag-theme-alpine-dark"
      style={{ width: "100%", height: 600 }}
    >
      <AgGridReact
        rowData={albums}
        columnDefs={columnDefs} // Column Defs for Columns
        animateRows={true}
      />
    </div>
  );
};
export default AlbumGrid;
