/*
 * ColMajorCell.java   Jul 14, 2004
 *
 * Copyright (c) 2004 Stan Salvador
 * stansalvador@hotmail.com
 */

package com.fastdtw.matrix;

public final class ColMajorCell {
    
    private final int col;
    private final int row;

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    public ColMajorCell(int column, int row) {
        this.col = column;
        this.row = row;
    } // end Constructor

    @Override
    public boolean equals(Object o) {
        return (o instanceof ColMajorCell) && (((ColMajorCell) o).col == this.col)
                && (((ColMajorCell) o).row == this.row);
    }

    @Override
    public int hashCode() {
        return (1 << col) + row;
    }

    @Override
    public String toString() {
        return "(" + col + "," + row + ")";
    }

} 
