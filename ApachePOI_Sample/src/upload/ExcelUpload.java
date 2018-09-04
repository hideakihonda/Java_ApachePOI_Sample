package upload;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;

import util.UtilPropertyManager;

public class ExcelUpload<D> {

	HSSFWorkbook book;

	public HSSFWorkbook getBook() {
		return book;
	}

	public void setBook(HSSFWorkbook book) {
		this.book = book;
	}

	String propertyFileName;

	public String getPropertyFileName() {
		return propertyFileName;
	}

	public void setPropertyFileName(String propertyFileName) {
		this.propertyFileName = propertyFileName;
	}

	String beanName;

	public String getBeanName() {
		return beanName;
	}

	public void setBeanName(String beanName) {
		this.beanName = beanName;
	}

	/*
	 * Excelファイルの読み込み
	 */
	public void readFile(InputStream file) throws IOException {

		//1.ファイルの存在チェック
		if(file == null || file.available() <= 0){
			System.out.println("ファイルが存在しません");
		}

		//2.ファイルの読み込み
		POIFSFileSystem fileSystem = new POIFSFileSystem(file);
		HSSFWorkbook book = new HSSFWorkbook(fileSystem);
		setBook(book);
	}

	/*
	 * シートの読み込み
	 */
	public List<D> readSheet(String sheetName) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException, ClassNotFoundException {
		HSSFSheet sheet = getBook().getSheet(sheetName);

		List<D> resultList = new ArrayList<D>();

		int cnrrentRow = 0;                  // 現在行
		int lastRow = sheet.getLastRowNum(); // 最終行

		while(cnrrentRow <= lastRow){

			//タイトル行のスキップ
			if (true) {

			}

			D bean = readRow(sheet.getRow(cnrrentRow));

			if (bean != null) {
				resultList.add(bean);
			}

			cnrrentRow++;
		}

		return resultList;
	}

	/*
	 * 行の読み込み
	 */
	private D readRow(HSSFRow row) throws IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException, ClassNotFoundException {

		if(row == null || row.getPhysicalNumberOfCells() <= 0){
			return null;
		} else {
			@SuppressWarnings("unchecked")
			D bean = (D)Class.forName(getBeanName()).newInstance();

			UtilPropertyManager propUtil = new UtilPropertyManager(propertyFileName);

			for (Entry<Object, Object> entry : propUtil.getProp().entrySet()) {
				if (entry.getKey().toString().startsWith("beanSet")) {
					String value = entry.getValue().toString();

					String[] values = value.split(",");
					String propertyName = values[0];
					int    columnNo     = Integer.valueOf(values[1]);
					int    cellType     = Integer.valueOf(values[2]);

					BeanUtils.setProperty(bean, propertyName, readCell(row.getCell(columnNo), cellType));
				}
			}

			return bean;
		}
	}

	/*
	 * セルの読み込み
	 */
	private String readCell(HSSFCell cell, int numType) {

		String val = "";

		int type = 3;//デフォルトのセルの型はブランク

		if(cell != null){
			type = cell.getCellType();
		}

		switch(type){
			case HSSFCell.CELL_TYPE_NUMERIC:
				if(numType == 1){
					val = String.valueOf((int)cell.getNumericCellValue());
			    	break;

				}else if(numType == 2){
					val = String.valueOf((long)cell.getNumericCellValue());
			    	break;

				}else if(numType == 3){
					val = String.valueOf((double)cell.getNumericCellValue());
			    	break;

				}else{
					val = String.valueOf((int)cell.getNumericCellValue());
			    	break;
				}

			case HSSFCell.CELL_TYPE_STRING:
				val = cell.getRichStringCellValue().toString();
				break;

			case HSSFCell.CELL_TYPE_BLANK:
				break;

			case HSSFCell.CELL_TYPE_FORMULA:
				val = cell.getCellFormula().toString();
				break;

			default:
				val = cell.getRichStringCellValue().toString();
				break;
		}

		if(val == null){
			val = "";
		}

		return val;
	}
}
