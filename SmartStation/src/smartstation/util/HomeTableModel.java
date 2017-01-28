package smartstation.util;

import javax.swing.table.AbstractTableModel;

public class HomeTableModel extends AbstractTableModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4968417102782493903L;

	private String[] columnNames;
	private Object[][] data;

	public HomeTableModel(String[] columnNames, Object[][] data) {
		this.columnNames = columnNames;
		this.data = data;
		//fireTableDataChanged();
	}

	@Override
	public int getColumnCount() {
		return columnNames.length;
	}

	@Override
	public int getRowCount() {
		if(data!=null)
		return data.length;
		
		return 0;
	}

	@Override
	public Object getValueAt(int row, int col) {
		return data[row][col];
	}

	public String getColumnName(int col) {
		return columnNames[col];
	}

	public Class<?> getColumnClass(int c) {
		return getValueAt(0, c).getClass();
	}

	public void setValueAt(Object value, int row, int col) {
		data[row][col] = value;
		fireTableCellUpdated(row, col);
	}

}
