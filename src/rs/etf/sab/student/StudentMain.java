package rs.etf.sab.student;

import rs.etf.sab.student.AddressOp;
import rs.etf.sab.student.CityOp;
import rs.etf.sab.operations.*;
import rs.etf.sab.tests.TestHandler;
import rs.etf.sab.tests.TestRunner;


public class StudentMain {

    public static void main(String[] args) {
        AddressOperations addressOperations = new AddressOp(); // Change this to your implementation.
        CityOperations cityOperations = new CityOp(); // Do it for all classes.
        CourierOperations courierOperations = new CourierOp(); // e.g. = new MyDistrictOperations();
        CourierRequestOperation courierRequestOperation = new CourierRequestOp();
        DriveOperation driveOperation = null;
        GeneralOperations generalOperations = new generalOp();
        PackageOperations packageOperations = new PackageOp();
        StockroomOperations stockroomOperations = new StockroomOp();
        UserOperations userOperations = new UserOp();
        VehicleOperations vehicleOperations = new VehicleOp();


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
