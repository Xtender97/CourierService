package kurirskasluzba;

import kurirskasluzba.AddressOp;
import kurirskasluzba.CityOp;
import rs.etf.sab.operations.*;
import rs.etf.sab.tests.TestHandler;
import rs.etf.sab.tests.TestRunner;


public class StudentMain {

    public static void main(String[] args) {
        AddressOperations addressOperations = new AddressOp(); // Change this to your implementation.
        CityOperations cityOperations = new CityOp(); // Do it for all classes.
        CourierOperations courierOperations = null; // e.g. = new MyDistrictOperations();
        CourierRequestOperation courierRequestOperation = null;
        DriveOperation driveOperation = null;
        GeneralOperations generalOperations = null;
        PackageOperations packageOperations = null;
        StockroomOperations stockroomOperations = null;
        UserOperations userOperations = null;
        VehicleOperations vehicleOperations = null;


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
