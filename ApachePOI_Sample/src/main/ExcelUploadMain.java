package main;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import upload.EmployeeBean;
import upload.ExcelUpload;

public class ExcelUploadMain {

	public static void main(String[] args) throws IOException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InstantiationException, ClassNotFoundException {

		ExcelUpload<EmployeeBean> excel = new ExcelUpload<EmployeeBean>();

		//srcの下のuploadパッケージにあるsample.xlsを読み込みます。
		excel.readFile(ExcelUploadMain.class.getClassLoader().getResourceAsStream("upload\\sample.xls"));
		//excel.readFile(new FileInputStream("C:\\temp\\sample.xls"));

		//Excelファイルの解析に使用する設定ファイルを指定します。
		excel.setPropertyFileName("upload\\employee.properties");

		//解析したデータを格納するBeanクラスを指定します。
		excel.setBeanName("upload.EmployeeBean");

		//解析対象のシート名を指定します。
		List<EmployeeBean> employeelist = excel.readSheet("Sheet1");

		for (EmployeeBean bean : employeelist) {
			System.out.println("社員番号=" + bean.getEmployeeNo() + " 名前=" + bean.getName() + " 役職コード=" + bean.getRoleCode());
		}
	}
}
