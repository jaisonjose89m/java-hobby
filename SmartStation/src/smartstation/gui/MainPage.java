package smartstation.gui;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import smartstation.util.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import smartstation.entities.Customer;
import smartstation.entities.Model;
import smartstation.entities.Vehicle;
import smartstation.logging.SmartLogger;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.border.LineBorder;

import java.awt.BorderLayout;
import java.awt.Color;

import javax.swing.JPasswordField;
import javax.swing.border.EtchedBorder;

import java.awt.SystemColor;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;

public class MainPage {
	private static final String USERNAME_KEY = "javax.persistence.jdbc.user";
	private static final String PASSWORD_KEY = "javax.persistence.jdbc.password";
	private static final String PERSISTANCE_URL = "javax.persistence.jdbc.url";
	private JFrame mainPage;
	private SmartLogger logger;
	private JTextField txtVehNum;
	private JTextField txtLastServiceKms;
	private JTextField txtCompanyName;
	private JTextField txtModel;
	private JTextField txtVariant;
	private JTextField txtServiceFreq;
	private JTextField txtCustomerName;
	private JTextField txtContactNum;
	private JTextField txtYearOfPurchase;
	private JTextArea textAreaAddress;
	private JTextField txtGetCustVehNum;
	// Customer id to backup the id of object got from db
	private Long customerId = null;
	private JTextField txtSearchVehNum;
	private JTextField txtSearchLastSerKms;
	private JTextField txtSearchYrOfPur;
	private JTextField txtSearchCompName;
	private JTextField txtSearchModel;
	private JTextField txtSearchVariant;
	private JTextField txtSearchSerFreq;
	private JTextField txtSearchCustName;
	private JTextField txtSearchContactNum;
	private JTextArea textAreaSearch;
	private JComboBox<String> comboDay;
	private JComboBox<String> comboMonth;
	private JComboBox<String> comboYear;
	private JComboBox<String> comboBoxDay;
	private JComboBox<String> comboBoxMonth;
	private JComboBox<String> comboBoxYear;
	private JTextField txtDays;
	private JTable table;
	private JTextField txtUserName;
	private JPasswordField pwdOldPassword;
	private JPasswordField pwdNewPassword;
	
	private String username;
	private String password;

	public MainPage(final String username, final String password) {
		
		this.username = username;
		this.password = password;
		
		logger = new SmartLogger();
		
		logger.getLogger().info("Loading Main Page");
		
		// Pass username and password in properties map
		Map<String, String> properties = new HashMap<String, String>();
		properties.put(USERNAME_KEY, username);
		properties.put(PASSWORD_KEY, password);
		properties.put(PERSISTANCE_URL, "jdbc:mysql://localhost:3306/"+Constants.DB_NAME);
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			logger.getLogger().warning("Unable to get system look and feel");
		}
		mainPage = new JFrame("Smart Station");
		mainPage.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainPage.getContentPane().setLayout(new BorderLayout());
		

		// Set Icon for JFrame
		ImageIcon icon = new ImageIcon("icon/car_32.png");
		mainPage.setIconImage(icon.getImage());
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP,
				JTabbedPane.SCROLL_TAB_LAYOUT);
		tabbedPane.setBounds(10, 11, 680, 430);
		mainPage.getContentPane().add(tabbedPane,BorderLayout.CENTER);
		
		
		// Change password tab
		JPanel panelChangePwd = new JPanel();
		panelChangePwd.setBounds(10, 11, 650, 339);
		
		panelChangePwd.setLayout(null);
		
		JPanel panel = new JPanel();
		panel.setBorder(new EtchedBorder(EtchedBorder.RAISED, SystemColor.activeCaption, null));
		panel.setBounds(37, 50, 444, 260);
		panelChangePwd.add(panel);
		panel.setLayout(null);
		
		JLabel lblUsername = new JLabel("Username");
		lblUsername.setBounds(37, 35, 72, 14);
		panel.add(lblUsername);
		
		txtUserName = new JTextField();
		txtUserName.setBounds(162, 32, 260, 20);
		panel.add(txtUserName);
		txtUserName.setColumns(10);
		
		JLabel lblOldPassword = new JLabel("Old Password");
		lblOldPassword.setBounds(37, 87, 72, 14);
		panel.add(lblOldPassword);
		
		pwdOldPassword = new JPasswordField();
		pwdOldPassword.setBounds(162, 84, 260, 20);
		panel.add(pwdOldPassword);
		
		JLabel lblNewPassword = new JLabel("New Password");
		lblNewPassword.setBounds(37, 135, 84, 14);
		panel.add(lblNewPassword);
		
		pwdNewPassword = new JPasswordField();
		pwdNewPassword.setBounds(162, 132, 260, 20);
		panel.add(pwdNewPassword);
		
		JButton btnChange = new JButton("Change");
		btnChange.setBounds(198, 195, 89, 23);
		panel.add(btnChange);
		
		btnChange.addActionListener(ev->{
			// Get the user details
			String user = txtUserName.getText();
			String oldPwd = new String(pwdOldPassword.getPassword());
			String newPwd = new String(pwdNewPassword.getPassword());
			
			if(validateUser(user, oldPwd, newPwd))
			{
				Connection connect;
				try {
					connect = DriverManager
							.getConnection("jdbc:mysql://localhost/"
									+ Constants.DB_NAME + "?" + "user=" + user
									+ "&password=" + oldPwd);
					
					Statement stmt = connect.createStatement();
					stmt.execute("ALTER USER '"+ user +"'@'localhost' IDENTIFIED BY '" + newPwd + "'");
					this.password = newPwd;
					connect.close();
					JOptionPane.showMessageDialog(mainPage, "Password updated");
				} catch (Exception e) {
					logger.getLogger().warning("Unable to change the user password for user " + user);
				}
				
			}
			/*else
			{
				JOptionPane.showMessageDialog(mainPage, "Invalid user " + user, "Warning", JOptionPane.WARNING_MESSAGE);
			}*/
			
		});
		
		JButton btnClearPwd = new JButton("Clear");
		btnClearPwd.setBounds(314, 195, 89, 23);
		panel.add(btnClearPwd);
		btnClearPwd.addActionListener(ev->{
			txtUserName.setText("");
			pwdOldPassword.setText("");
			pwdNewPassword.setText("");
		});
		
		// Tab for Home panel
		JPanel panelHome = new JPanel();
		panelHome.setBorder(new EtchedBorder(EtchedBorder.RAISED, SystemColor.activeCaptionBorder, null));
		panelHome.setBounds(10, 11, 650, 339);

		JPanel panelHomeSearch = new JPanel();

		JLabel lblVehiclesToBe = new JLabel("Vehicles to be serviced in next ");

		txtDays = new JTextField();
		txtDays.setText("10");
		txtDays.setColumns(10);

		JLabel lblDays = new JLabel("days");

		JButton btnGetVehicles = new JButton("Get Vehicles");

		// Handler for Get vehicles btn
		btnGetVehicles.addActionListener(ev -> {
			// Get the vehicles with next service day is in next x days
				setTableContents(txtDays.getText());
			});

		JPanel panelTable = new JPanel();

		JScrollPane tableScrollPane = new JScrollPane();
		tableScrollPane.setViewportBorder(new LineBorder(new Color(0, 0, 0), 1,
				true));

		table = new JTable();
		table.setForeground(Color.WHITE);
		table.setBackground(new Color(0, 102, 102));
		logger.getLogger().info("Getting the info for next 10 days");
		setTableContents("10");
		logger.getLogger().info("Collected the info for next 10 days");
		if(table.getColumnModel().getColumnCount() >= 3)
		{
			table.getColumnModel().getColumn(0).setPreferredWidth(143);
			table.getColumnModel().getColumn(1).setPreferredWidth(162);
			table.getColumnModel().getColumn(2).setPreferredWidth(136);
		}
		table.setBorder(null);
		table.setFillsViewportHeight(true);
		table.setBounds(10, 11, 640, 295);
		table.setFillsViewportHeight(true);

		// panel.add(table);
		tableScrollPane.setViewportView(table);

		// Tab for search
		JPanel panelSearch = new JPanel();
		panelSearch.setBorder(new EtchedBorder(EtchedBorder.RAISED, SystemColor.activeCaptionBorder, null));
		panelSearch.setBounds(10, 11, 650, 339);
		
		panelSearch.setLayout(null);

		JPanel panelSearchVehNum = new JPanel();
		panelSearchVehNum.setBounds(10, 11, 623, 38);
		panelSearch.add(panelSearchVehNum);
		panelSearchVehNum.setLayout(null);

		JLabel lblVehicleNumber_1 = new JLabel("Vehicle Number");
		lblVehicleNumber_1.setBounds(10, 11, 80, 14);
		panelSearchVehNum.add(lblVehicleNumber_1);

		txtSearchVehNum = new JTextField();
		txtSearchVehNum.setBounds(108, 8, 159, 20);
		panelSearchVehNum.add(txtSearchVehNum);
		txtSearchVehNum.setColumns(10);

		JButton btnSearch = new JButton("Search");
		btnSearch.setBounds(290, 7, 89, 23);
		panelSearchVehNum.add(btnSearch);
		btnSearch.addActionListener(ev -> {
			// Get vehicle info from db
				EntityManagerFactory factory = Persistence
						.createEntityManagerFactory(
								Constants.PERSISTANCE_UNIT_NAME, properties);
				EntityManager em = factory.createEntityManager();

				Vehicle vehicle = em.find(Vehicle.class,
						txtSearchVehNum.getText());

				if (vehicle != null) {
					txtSearchCompName.setText(vehicle.getModel()
							.getCompanyName());
					txtSearchContactNum.setText(vehicle.getCustomer()
							.getContactNum() + "");
					txtSearchCustName.setText(vehicle.getCustomer().getName());
					txtSearchLastSerKms.setText(vehicle.getLastServiceKms()
							+ "");
					txtSearchModel.setText(vehicle.getModel().getModel());
					txtSearchSerFreq.setText(vehicle.getModel()
							.getServiceFreq().toString());
					txtSearchVariant.setText(vehicle.getModel().getVariant());
					txtSearchYrOfPur.setText(vehicle.getYearOfPurchase() + "");
					txtServiceFreq.setText(vehicle.getModel().getServiceFreq()
							+ "");
					textAreaSearch.setText(vehicle.getCustomer().getAddress());
					// Get the last service date and populate the comboboxes
				Date date = vehicle.getLastServiceDate();
				Calendar cal = Calendar.getInstance();
				cal.setTime(date);

				comboDay.setSelectedItem("" + cal.get(Calendar.DAY_OF_MONTH));
				comboMonth.setSelectedItem("" + (cal.get(Calendar.MONTH) + 1));
				comboYear.setSelectedItem("" + cal.get(Calendar.YEAR));
			}

			em.close();
			factory.close();
		});

		JPanel panelSearchVeh = new JPanel();
		panelSearchVeh.setLayout(null);
		panelSearchVeh.setBounds(10, 60, 283, 98);
		panelSearch.add(panelSearchVeh);

		JLabel lblSearchLastServiceDate = new JLabel("Last Service Date");
		lblSearchLastServiceDate.setBounds(10, 11, 87, 14);
		panelSearchVeh.add(lblSearchLastServiceDate);

		JLabel lblSearchLastSerKms = new JLabel("Last Service Kms");
		lblSearchLastSerKms.setBounds(10, 39, 87, 14);
		panelSearchVeh.add(lblSearchLastSerKms);

		txtSearchLastSerKms = new JTextField();
		txtSearchLastSerKms.setColumns(10);
		txtSearchLastSerKms.setBounds(108, 36, 166, 20);
		panelSearchVeh.add(txtSearchLastSerKms);

		comboDay = new JComboBox<String>();
		comboDay.setBounds(108, 11, 43, 20);
		comboDay.setModel(new DefaultComboBoxModel<String>(getDays()));
		panelSearchVeh.add(comboDay);

		comboMonth = new JComboBox<String>();
		comboMonth.setBounds(155, 11, 50, 20);
		comboMonth.setModel(new DefaultComboBoxModel<String>(getMonths()));
		panelSearchVeh.add(comboMonth);

		comboYear = new JComboBox<String>();
		comboYear.setBounds(208, 11, 66, 20);
		comboYear.setModel(new DefaultComboBoxModel<String>(getYears()));
		panelSearchVeh.add(comboYear);

		JLabel lblSearchYrOfPur = new JLabel("Year of Purchase");
		lblSearchYrOfPur.setBounds(10, 67, 100, 14);
		panelSearchVeh.add(lblSearchYrOfPur);

		txtSearchYrOfPur = new JTextField();
		txtSearchYrOfPur.setColumns(10);
		txtSearchYrOfPur.setBounds(108, 67, 166, 20);
		panelSearchVeh.add(txtSearchYrOfPur);

		JPanel panelSearchModel = new JPanel();
		panelSearchModel.setLayout(null);
		panelSearchModel.setBounds(10, 171, 283, 139);
		panelSearch.add(panelSearchModel);

		JLabel lblSearchCompName = new JLabel("Company Name");
		lblSearchCompName.setBounds(10, 14, 87, 14);
		panelSearchModel.add(lblSearchCompName);

		txtSearchCompName = new JTextField();
		txtSearchCompName.setColumns(10);
		txtSearchCompName.setBounds(108, 11, 166, 20);
		panelSearchModel.add(txtSearchCompName);

		JLabel lblSearchModel = new JLabel("Model");
		lblSearchModel.setBounds(10, 45, 46, 14);
		panelSearchModel.add(lblSearchModel);

		txtSearchModel = new JTextField();
		txtSearchModel.setColumns(10);
		txtSearchModel.setBounds(108, 42, 166, 20);
		panelSearchModel.add(txtSearchModel);

		JLabel lblSearchVariant = new JLabel("Variant");
		lblSearchVariant.setBounds(10, 74, 46, 14);
		panelSearchModel.add(lblSearchVariant);

		txtSearchVariant = new JTextField();
		txtSearchVariant.setColumns(10);
		txtSearchVariant.setBounds(108, 71, 166, 20);
		panelSearchModel.add(txtSearchVariant);

		JLabel lblSearchSerFreq = new JLabel("Service Frequency");
		lblSearchSerFreq.setBounds(10, 105, 100, 14);
		panelSearchModel.add(lblSearchSerFreq);

		txtSearchSerFreq = new JTextField();
		txtSearchSerFreq.setColumns(10);
		txtSearchSerFreq.setBounds(108, 102, 166, 20);
		panelSearchModel.add(txtSearchSerFreq);

		JPanel panelSearchCust = new JPanel();
		panelSearchCust.setLayout(null);
		panelSearchCust.setBounds(303, 60, 330, 183);
		panelSearch.add(panelSearchCust);

		JLabel lblSearchCustName = new JLabel("Customer Name");
		lblSearchCustName.setBounds(10, 11, 84, 14);
		panelSearchCust.add(lblSearchCustName);

		txtSearchCustName = new JTextField();
		txtSearchCustName.setColumns(10);
		txtSearchCustName.setBounds(100, 8, 166, 20);
		panelSearchCust.add(txtSearchCustName);

		JLabel lblSearchAddress = new JLabel("Address");
		lblSearchAddress.setBounds(10, 36, 46, 14);
		panelSearchCust.add(lblSearchAddress);

		JLabel lblSearchContactNum = new JLabel("Contact No.");
		lblSearchContactNum.setBounds(10, 148, 84, 14);
		panelSearchCust.add(lblSearchContactNum);

		txtSearchContactNum = new JTextField();
		txtSearchContactNum.setColumns(10);
		txtSearchContactNum.setBounds(100, 148, 166, 20);
		panelSearchCust.add(txtSearchContactNum);

		JScrollPane scrollPaneSearch = new JScrollPane();
		scrollPaneSearch.setBounds(100, 39, 166, 102);
		panelSearchCust.add(scrollPaneSearch);

		textAreaSearch = new JTextArea();
		textAreaSearch.setWrapStyleWord(true);
		textAreaSearch.setLineWrap(true);
		scrollPaneSearch.setViewportView(textAreaSearch);

		JButton btnUpdate = new JButton("Update");
		btnUpdate.setBounds(320, 281, 89, 23);
		panelSearch.add(btnUpdate);
		btnUpdate.addActionListener(ev -> {
			// Get the vehicle from db and update the fields with current value
				EntityManagerFactory factory = Persistence
						.createEntityManagerFactory(
								Constants.PERSISTANCE_UNIT_NAME, properties);
				EntityManager em = factory.createEntityManager();

				em.getTransaction().begin();

				Vehicle vehicle = em.find(Vehicle.class,
						txtSearchVehNum.getText());

				if (vehicle != null) {
					// Currently all fields updated are last service kms, last
					// service date, year of purchase, service frequency,
					// cust name, cust contact num & cust address
				vehicle.setLastServiceKms(Integer.parseInt(txtSearchLastSerKms
						.getText()));
				vehicle.setYearOfPurchase(Integer.parseInt(txtSearchYrOfPur
						.getText()));
				vehicle.getCustomer().setName(txtSearchCustName.getText());
				vehicle.getCustomer().setAddress(textAreaSearch.getText());
				vehicle.getCustomer().setContactNum(
						Long.parseLong(txtSearchContactNum.getText()));
				vehicle.getModel().setServiceFreq(
						Integer.parseInt(txtSearchSerFreq.getText()));

				// Create the last service date
				String dateStr = comboDay.getSelectedItem().toString() + "-"
						+ comboMonth.getSelectedItem().toString() + "-"
						+ comboYear.getSelectedItem().toString();
				DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
				try {
					Date date = format.parse(dateStr);
					vehicle.setLastServiceDate(date);
				} catch (Exception e) {
					logger.getLogger().warning("Invalid Date !!!");
				}
			}

			em.getTransaction().commit();

			em.close();
			factory.close();
			JOptionPane.showMessageDialog(mainPage, "Updated Vehicle info");
		});

		JButton btnClearSearch = new JButton("Clear");
		btnClearSearch.setBounds(519, 281, 89, 23);
		panelSearch.add(btnClearSearch);

		JButton btnDelete = new JButton("Delete");
		btnDelete.setBounds(420, 281, 89, 23);
		panelSearch.add(btnDelete);
		btnDelete.addActionListener(ev -> {
			EntityManagerFactory factory = Persistence
					.createEntityManagerFactory(
							Constants.PERSISTANCE_UNIT_NAME, properties);
			EntityManager em = factory.createEntityManager();
			em.getTransaction().begin();

			// Delete vehicle info based on vehicle number
				Vehicle vehicle = em.find(Vehicle.class,
						txtSearchVehNum.getText());
				if (vehicle != null) {
					em.remove(vehicle);
				}

				em.getTransaction().commit();
				em.close();
				factory.close();
				JOptionPane.showMessageDialog(mainPage, "Vehicle Deleted");
				clearSearchPanel();
			});

		btnClearSearch.addActionListener(ev -> {
			clearSearchPanel();
		});

		// Tab for adding vehicle info
		JPanel panelAddVehicle = new JPanel();
		panelAddVehicle.setBorder(new EtchedBorder(EtchedBorder.RAISED, SystemColor.activeCaptionBorder, null));
		panelAddVehicle.setBounds(10, 11, 650, 339);
		// mainPage.getContentPane().add(panelAddVehicle);
		
		panelAddVehicle.setLayout(null);

		JPanel panelModel = new JPanel();
		panelModel.setBounds(27, 156, 283, 139);
		panelAddVehicle.add(panelModel);
		panelModel.setLayout(null);

		JLabel lblCompanyName = new JLabel("Company Name");
		lblCompanyName.setBounds(10, 14, 87, 14);
		panelModel.add(lblCompanyName);

		txtCompanyName = new JTextField();
		txtCompanyName.setBounds(108, 11, 166, 20);
		panelModel.add(txtCompanyName);
		txtCompanyName.setColumns(10);

		JLabel lblModel = new JLabel("Model");
		lblModel.setBounds(10, 45, 46, 14);
		panelModel.add(lblModel);

		txtModel = new JTextField();
		txtModel.setBounds(108, 42, 166, 20);
		panelModel.add(txtModel);
		txtModel.setColumns(10);

		JLabel lblVariant = new JLabel("Variant");
		lblVariant.setBounds(10, 74, 46, 14);
		panelModel.add(lblVariant);

		txtVariant = new JTextField();
		txtVariant.setBounds(108, 71, 166, 20);
		panelModel.add(txtVariant);
		txtVariant.setColumns(10);

		JLabel lblServiceFrequency = new JLabel("Service Frequency");
		lblServiceFrequency.setBounds(10, 105, 100, 14);
		panelModel.add(lblServiceFrequency);

		txtServiceFreq = new JTextField();
		txtServiceFreq.setColumns(10);
		txtServiceFreq.setBounds(108, 102, 166, 20);
		panelModel.add(txtServiceFreq);

		JPanel panelVehicle = new JPanel();
		panelVehicle.setBounds(27, 22, 283, 123);
		panelAddVehicle.add(panelVehicle);
		panelVehicle.setLayout(null);

		JLabel lblVehicleNumber = new JLabel("Vehicle Number");
		lblVehicleNumber.setBounds(10, 14, 87, 14);
		panelVehicle.add(lblVehicleNumber);

		txtVehNum = new JTextField();
		txtVehNum.setBounds(108, 11, 166, 20);
		panelVehicle.add(txtVehNum);
		txtVehNum.setColumns(10);

		JLabel lblLastServiceDate = new JLabel("Last Service Date");
		lblLastServiceDate.setBounds(10, 39, 87, 14);
		panelVehicle.add(lblLastServiceDate);

		JLabel lblLastServiceKms = new JLabel("Last Service Kms");
		lblLastServiceKms.setBounds(10, 67, 87, 14);
		panelVehicle.add(lblLastServiceKms);

		txtLastServiceKms = new JTextField();
		txtLastServiceKms.setBounds(108, 64, 166, 20);
		panelVehicle.add(txtLastServiceKms);
		txtLastServiceKms.setColumns(10);

		comboBoxDay = new JComboBox<String>();
		comboBoxDay.setBounds(108, 39, 43, 20);
		panelVehicle.add(comboBoxDay);
		comboBoxDay.setModel(new DefaultComboBoxModel<String>(getDays()));

		comboBoxMonth = new JComboBox<String>();
		comboBoxMonth.setBounds(155, 39, 50, 20);
		panelVehicle.add(comboBoxMonth);
		comboBoxMonth.setModel(new DefaultComboBoxModel<String>(getMonths()));

		comboBoxYear = new JComboBox<String>();
		comboBoxYear.setBounds(208, 39, 66, 20);
		panelVehicle.add(comboBoxYear);
		comboBoxYear.setModel(new DefaultComboBoxModel<String>(getYears()));

		JLabel lblYearOfPurchase = new JLabel("Year of Purchase");
		lblYearOfPurchase.setBounds(10, 95, 100, 14);
		panelVehicle.add(lblYearOfPurchase);

		txtYearOfPurchase = new JTextField();
		txtYearOfPurchase.setBounds(108, 95, 166, 20);
		panelVehicle.add(txtYearOfPurchase);
		txtYearOfPurchase.setColumns(10);

		JPanel panelCustomer = new JPanel();
		panelCustomer.setBounds(320, 112, 330, 183);
		panelAddVehicle.add(panelCustomer);
		panelCustomer.setLayout(null);

		JLabel lblCustomerName = new JLabel("Customer Name");
		lblCustomerName.setBounds(10, 11, 84, 14);
		panelCustomer.add(lblCustomerName);

		txtCustomerName = new JTextField();
		txtCustomerName.setColumns(10);
		txtCustomerName.setBounds(100, 8, 166, 20);
		panelCustomer.add(txtCustomerName);

		JLabel lblAddress = new JLabel("Address");
		lblAddress.setBounds(10, 36, 46, 14);
		panelCustomer.add(lblAddress);

		JLabel lblContactNo = new JLabel("Contact No.");
		lblContactNo.setBounds(10, 148, 84, 14);
		panelCustomer.add(lblContactNo);

		txtContactNum = new JTextField();
		txtContactNum.setColumns(10);
		txtContactNum.setBounds(100, 148, 166, 20);
		panelCustomer.add(txtContactNum);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(100, 39, 166, 102);
		panelCustomer.add(scrollPane);

		textAreaAddress = new JTextArea();
		scrollPane.setViewportView(textAreaAddress);
		textAreaAddress.setWrapStyleWord(true);
		textAreaAddress.setLineWrap(true);

		JButton btnAdd = new JButton("Add");
		btnAdd.setBounds(383, 305, 89, 23);
		panelAddVehicle.add(btnAdd);
		// Event handler for btnAdd
		btnAdd.addActionListener(ev -> {

			EntityManagerFactory factory = Persistence
					.createEntityManagerFactory(
							Constants.PERSISTANCE_UNIT_NAME, properties);
			EntityManager em = factory.createEntityManager();

			em.getTransaction().begin();

			// Model for the vehicle
			String compName = txtCompanyName.getText();
			String modelName = txtModel.getText();
			String variant = txtVariant.getText();
			Integer serFreq = Integer.parseInt(txtServiceFreq.getText());

			// Check if model already exist in db
			Query getModelQuery = em
					.createQuery("Select m from Model m where m.companyName='"
							+ compName + "' AND m.model='" + modelName
							+ "' AND m.variant='" + variant
							+ "' AND m.serviceFreq='" + serFreq + "'");
			Model model = null;
			try {
				model = (Model) getModelQuery.getSingleResult();
			} catch (NoResultException nre) {
				// Model not available already
				// No action required
			}

			if (model == null) {
				// Create model object
				model = new Model();
				model.setCompanyName(compName);
				model.setModel(modelName);
				model.setVariant(variant);
				model.setServiceFreq(serFreq);
			}

			// Create Customer object if not available
			Customer customer = null;
			if (customerId == null) {
				customer = new Customer();
				customer.setAddress(textAreaAddress.getText());
				customer.setContactNum(Long.parseLong(txtContactNum.getText()));
				customer.setName(txtCustomerName.getText());
			} else {
				customer = em.find(Customer.class, customerId);
				// After use make it null
				customerId = null;
			}

			// Create Vehicle object
			Vehicle vehicle = new Vehicle();
			vehicle.setCustomer(customer);
			String dateStr = comboBoxDay.getSelectedItem().toString() + "-"
					+ comboBoxMonth.getSelectedItem().toString() + "-"
					+ comboBoxYear.getSelectedItem().toString();
			DateFormat format = new SimpleDateFormat("dd-MM-yyyy");
			try {
				Date date = format.parse(dateStr);
				vehicle.setLastServiceDate(date);
			} catch (Exception e) {
				logger.getLogger().warning("Invalid Date !!!");
			}
			vehicle.setLastServiceKms(Integer.parseInt(txtLastServiceKms
					.getText()));
			vehicle.setModel(model);
			vehicle.setVehNum(txtVehNum.getText());
			vehicle.setYearOfPurchase(Integer.parseInt(txtYearOfPurchase
					.getText()));
			// vehicle.setLastServiceDate(Date.parse(txtLastServiceDate.getText()));

			em.persist(vehicle);
			em.getTransaction().commit();

			em.close();
			factory.close();

			JOptionPane.showMessageDialog(mainPage, "Vehicle info added");
		});

		JButton btnClear = new JButton("Clear");
		btnClear.setBounds(492, 305, 89, 23);
		panelAddVehicle.add(btnClear);

		JPanel panelGetCustomerInfo = new JPanel();
		panelGetCustomerInfo.setBounds(320, 24, 330, 77);
		panelAddVehicle.add(panelGetCustomerInfo);
		panelGetCustomerInfo.setLayout(null);

		JCheckBox chckbxGetCustomerInfo = new JCheckBox(
				"Get Customer Info from Vehicle Number");
		chckbxGetCustomerInfo.setBounds(6, 7, 279, 23);
		panelGetCustomerInfo.add(chckbxGetCustomerInfo);
		chckbxGetCustomerInfo.addActionListener(ev -> {
			if (chckbxGetCustomerInfo.isSelected()) {
				// Enable the vehicle number text field
				txtGetCustVehNum.setEnabled(true);
				// Disable the customer info panel elements
				disableCustomerPanel();
			} else {
				txtGetCustVehNum.setEnabled(false);
				enableCustomerPanel();
			}
		});

		JLabel lblGetCustVehNum = new JLabel("Vehicle Number");
		lblGetCustVehNum.setBounds(10, 49, 87, 14);
		panelGetCustomerInfo.add(lblGetCustVehNum);

		txtGetCustVehNum = new JTextField();
		txtGetCustVehNum.setEnabled(false);
		txtGetCustVehNum.setColumns(10);
		txtGetCustVehNum.setBounds(100, 46, 166, 20);
		panelGetCustomerInfo.add(txtGetCustVehNum);

		JButton btnGet = new JButton("Get");
		btnGet.setBounds(276, 45, 50, 23);
		panelGetCustomerInfo.add(btnGet);

		// Event handler for Get button
		btnGet.addActionListener(ev -> {
			String vehNum = txtGetCustVehNum.getText();
			if (isValidVehNum(vehNum)) {
				// Get the customer info using the vehicle number
				EntityManagerFactory factory = Persistence
						.createEntityManagerFactory(
								Constants.PERSISTANCE_UNIT_NAME, properties);
				EntityManager em = factory.createEntityManager();
				em.getTransaction().begin();

				Query getVehicleQuery = em.createQuery(
						"SELECT v FROM Vehicle v WHERE v.vehNum='" + vehNum
								+ "'", Vehicle.class);

				try {
					Vehicle vehicle = (Vehicle) getVehicleQuery
							.getSingleResult();

					if (vehicle != null && vehicle.getCustomer() != null) {
						// Back up for adding to db
						customerId = vehicle.getCustomer().getId();
						txtCustomerName
								.setText(vehicle.getCustomer().getName());
						txtContactNum.setText(vehicle.getCustomer()
								.getContactNum().toString());
						textAreaAddress.setText(vehicle.getCustomer()
								.getAddress());
					}
				} catch (NoResultException nre) {
					JOptionPane.showMessageDialog(mainPage,
							"No customer details found for vehicle number "
									+ vehNum, "warning",
							JOptionPane.WARNING_MESSAGE);
				}
				em.close();
				factory.close();
			} else {
				JOptionPane.showMessageDialog(mainPage,
						"Invalid Vehicle Number", "Warning",
						JOptionPane.WARNING_MESSAGE);
			}
		});

		// Event handler for btnClear
		btnClear.addActionListener(ev -> {
			// Clear all the inputs
			clear();
		});
		// TODO order tabs
		tabbedPane.addTab("Home", panelHome);
		GroupLayout gl_panelHome = new GroupLayout(panelHome);
		gl_panelHome.setHorizontalGroup(
			gl_panelHome.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelHome.createSequentialGroup()
					.addGap(8)
					.addGroup(gl_panelHome.createParallelGroup(Alignment.LEADING)
						.addComponent(panelHomeSearch, GroupLayout.DEFAULT_SIZE, 660, Short.MAX_VALUE)
						.addComponent(panelTable, GroupLayout.PREFERRED_SIZE, 660, GroupLayout.PREFERRED_SIZE))
					.addGap(7))
		);
		gl_panelHome.setVerticalGroup(
			gl_panelHome.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelHome.createSequentialGroup()
					.addGap(9)
					.addComponent(panelHomeSearch, GroupLayout.PREFERRED_SIZE, 39, GroupLayout.PREFERRED_SIZE)
					.addGap(11)
					.addComponent(panelTable, GroupLayout.PREFERRED_SIZE, 317, GroupLayout.PREFERRED_SIZE))
		);
		gl_panelHome.setAutoCreateContainerGaps(true);
		gl_panelHome.setAutoCreateGaps(true);
		GroupLayout gl_panelTable = new GroupLayout(panelTable);
		gl_panelTable.setAutoCreateContainerGaps(true);
		gl_panelTable.setAutoCreateGaps(true);
		gl_panelTable.setHorizontalGroup(
			gl_panelTable.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelTable.createSequentialGroup()
					.addGap(10)
					.addComponent(tableScrollPane, GroupLayout.PREFERRED_SIZE, 640, GroupLayout.PREFERRED_SIZE))
		);
		gl_panelTable.setVerticalGroup(
			gl_panelTable.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelTable.createSequentialGroup()
					.addGap(11)
					.addComponent(tableScrollPane, GroupLayout.PREFERRED_SIZE, 295, GroupLayout.PREFERRED_SIZE))
		);
		panelTable.setLayout(gl_panelTable);
		GroupLayout gl_panelHomeSearch = new GroupLayout(panelHomeSearch);
		gl_panelHomeSearch.setHorizontalGroup(
			gl_panelHomeSearch.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelHomeSearch.createSequentialGroup()
					.addGap(10)
					.addComponent(lblVehiclesToBe, GroupLayout.DEFAULT_SIZE, 148, Short.MAX_VALUE)
					.addGap(20)
					.addComponent(txtDays, GroupLayout.DEFAULT_SIZE, 54, Short.MAX_VALUE)
					.addGap(31)
					.addGroup(gl_panelHomeSearch.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_panelHomeSearch.createSequentialGroup()
							.addGap(40)
							.addComponent(btnGetVehicles, GroupLayout.PREFERRED_SIZE, 107, GroupLayout.PREFERRED_SIZE))
						.addGroup(gl_panelHomeSearch.createSequentialGroup()
							.addComponent(lblDays, GroupLayout.DEFAULT_SIZE, 46, Short.MAX_VALUE)
							.addGap(101)))
					.addGap(250))
		);
		gl_panelHomeSearch.setVerticalGroup(
			gl_panelHomeSearch.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_panelHomeSearch.createSequentialGroup()
					.addGap(11)
					.addComponent(lblVehiclesToBe))
				.addGroup(gl_panelHomeSearch.createSequentialGroup()
					.addGap(8)
					.addComponent(txtDays, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
				.addGroup(gl_panelHomeSearch.createSequentialGroup()
					.addGap(7)
					.addGroup(gl_panelHomeSearch.createParallelGroup(Alignment.LEADING)
						.addComponent(btnGetVehicles)
						.addGroup(gl_panelHomeSearch.createSequentialGroup()
							.addGap(4)
							.addComponent(lblDays))))
		);
		gl_panelHomeSearch.setAutoCreateContainerGaps(true);
		gl_panelHomeSearch.setAutoCreateGaps(true);
		panelHomeSearch.setLayout(gl_panelHomeSearch);
		panelHome.setLayout(gl_panelHome);
		tabbedPane.addTab("Add Vehicle", panelAddVehicle);
		tabbedPane.addTab("Search & Update", panelSearch);
		tabbedPane.addTab("Change Password", panelChangePwd);
		
		mainPage.setBounds(0, 0, 700, 481);
		mainPage.setLocationRelativeTo(null);
		mainPage.setVisible(true);
	}

	private boolean validateUser(final String user, final String oldPwd, final String newPwd) {
		
		if(user != null && oldPwd != null && authenticate(user, oldPwd, Constants.DB_NAME))
		{
			if(newPwd != null )
			{
				return true;
			}else
			{
				JOptionPane.showMessageDialog(mainPage, "Invalid new password", "Warning", JOptionPane.WARNING_MESSAGE);
			}
		}else
		{
			JOptionPane.showMessageDialog(mainPage, "Invalid user details", "Error", JOptionPane.ERROR_MESSAGE);
		}
		
		return false;
	}

	public boolean authenticate(final String username, final String password,
			final String database) {
		try {
			Class.forName(Constants.MYSQL_DRIVER);
		} catch (ClassNotFoundException e1) {
			// System.out.println("MySql Database driver not found!!!");
			logger.getLogger().severe("MySql driver not found !!!");
		}
		// Setup the connection with the DB with given credentials
		try {
			Connection connect = DriverManager
					.getConnection("jdbc:mysql://localhost/" + database + "?"
							+ "user=" + username + "&password=" + password);
			connect.close();
		} catch (Exception e) {
			return false;
		}

		return true;
	}
	
	private void clearSearchPanel() {
		txtSearchCompName.setText("");
		txtSearchContactNum.setText("");
		txtSearchCustName.setText("");
		txtSearchLastSerKms.setText("");
		txtSearchModel.setText("");
		txtSearchSerFreq.setText("");
		txtSearchVariant.setText("");
		txtSearchVehNum.setText("");
		txtSearchYrOfPur.setText("");
		txtServiceFreq.setText("");
		textAreaSearch.setText("");
		comboDay.setSelectedIndex(0);
		comboMonth.setSelectedIndex(0);
		comboYear.setSelectedIndex(0);
	}

	private void enableCustomerPanel() {
		txtCustomerName.setEditable(true);
		txtContactNum.setEditable(true);
		textAreaAddress.setEditable(true);
	}

	private boolean isValidVehNum(final String vehNum) {
		if (vehNum != null && !vehNum.equals("")) {
			return true;
		}
		return false;
	}

	private void clear() {
		// Clear all the inputs
		txtCompanyName.setText("");
		txtContactNum.setText("");
		txtCustomerName.setText("");
		textAreaAddress.setText("");
		txtLastServiceKms.setText("");
		txtModel.setText("");
		txtServiceFreq.setText("");
		txtVariant.setText("");
		txtVehNum.setText("");
		txtYearOfPurchase.setText("");
		txtGetCustVehNum.setText("");
		comboBoxDay.setSelectedIndex(0);
		comboBoxMonth.setSelectedIndex(0);
		comboBoxYear.setSelectedIndex(0);
	}

	private void disableCustomerPanel() {
		txtCustomerName.setEditable(false);
		txtContactNum.setEditable(false);
		textAreaAddress.setEditable(false);
	}

	private String[] getDays() {
		String[] days = new String[31];
		for (int i = 0; i < 31; i++) {
			days[i] = (i + 1) + "";
		}
		return days;
	}

	private String[] getMonths() {
		String[] months = new String[12];
		for (int i = 0; i < 12; i++) {
			months[i] = (i + 1) + "";
		}
		return months;
	}

	private String[] getYears() {
		String[] years = new String[200];
		for (int i = 0; i < 200; i++) {
			years[i] = (i + 1990) + "";
		}
		return years;
	}

	private void setTableContents(final String days) {
		Connection connect = null;
		// System.out.println(days);
		try {
			connect = DriverManager
					.getConnection("jdbc:mysql://localhost/"
							+ Constants.DB_NAME + "?" + "user=" + this.username
							+ "&password=" + this.password);
			Statement stmt = connect.createStatement();
			ResultSet resultSet = stmt
					.executeQuery("SELECT * FROM Vehicle v, Model m, Customer c "
							+ "WHERE datediff(current_date(),LASTSERVICEDATE)>= (m.servicefreq*30 - "
							+ days
							+ ") AND "
							+ "datediff(current_date(),LASTSERVICEDATE) <= (m.servicefreq*30) AND v.model_id=m.id "
							+ "AND v.customer_id=c.id");
			String[] columnNames = { "Vehicle Number", "Customer Name",
					"Contact Number" };
			// System.out.println(resultSet.);
			resultSet.last();
			Object[][] data = null;
			if(resultSet.getRow() > 0)
			{
			data = 	new Object[resultSet.getRow()][3];
			// System.out.println(resultSet.getRow());
			int i = 0;
			resultSet.beforeFirst();
			while (resultSet.next()) {
				data[i][0] = resultSet.getNString("vehnum");
				data[i][1] = resultSet.getNString("name");
				data[i][2] = resultSet.getLong("contactnum");
				i++;
				// System.out.println(resultSet.getNString("vehnum"));
				// System.out.println(resultSet.getNString("name"));
				// System.out.println(resultSet.getLong("contactnum"));
			}
			}
			HomeTableModel tableModel = new HomeTableModel(columnNames, data);
			table.setModel(tableModel);

			connect.close();
		} catch (SQLException e) {
			logger.getLogger().warning("Unable to get the vehicle info for next " + days + " days");
			//table.setModel(null);
		}
		finally
		{
			if(connect != null)
			{
				try {
					connect.close();
				} catch (SQLException e) {
					logger.getLogger().warning("Unable to close db connection");
				}
			}
		}
	}
/*	public static void main(String[] args) {
		new MainPage("", "");
	}*/
}
