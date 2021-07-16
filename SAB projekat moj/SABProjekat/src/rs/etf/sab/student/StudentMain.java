package rs.etf.sab.student;

import java.math.BigDecimal;
import java.sql.*;

import rs.etf.sab.operations.*;
import rs.etf.sab.tests.TestHandler;
import rs.etf.sab.tests.TestRunner;

import java.util.List;


public class StudentMain {
     
    public static void main(String[] args) {
 
        AddressOperations addressOperations = new gd140092_AddressOperations(); // Change this to your implementation.
        CityOperations cityOperations = new gd140092_CityOperations(); // Do it for all classes.
        CourierOperations courierOperations = new gd140092_CourierOperations(); // e.g. = new MyDistrictOperations();
        CourierRequestOperation courierRequestOperation = new gd140092_CourierRequestOperation();
        DriveOperation driveOperation = new gd140092_DriveOperation();
        GeneralOperations generalOperations = new gd140092_GeneralOperations();
        PackageOperations packageOperations = new gd140092_PackageOperations();
        StockroomOperations stockroomOperations = new gd140092_StockroomOperations();
        UserOperations userOperations = new gd140092_UserOperations();
        VehicleOperations vehicleOperations = new gd140092_VehicleOperations();


        TestHandler.createInstance(
                addressOperations,
                cityOperations,
                courierOperations,
                courierRequestOperation,
                driveOperation,
                generalOperations,
                packageOperations,
                stockroomOperations,
                userOperations,
                vehicleOperations);

        TestRunner.runTests();  
       
    }
}