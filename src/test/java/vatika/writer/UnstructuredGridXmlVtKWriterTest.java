package vatika.writer;

import vatika.data.*;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.stream.Collectors;

import static vatika.data.VTKType.VTK_QUAD;
import static vatika.data.VTKType.VTK_TRIANGLE;
import static java.nio.ByteOrder.BIG_ENDIAN;
import static java.nio.ByteOrder.LITTLE_ENDIAN;
import static vatika.writer.DataFormat.ASCII;
import static vatika.writer.DataFormat.BINARY;

public class UnstructuredGridXmlVtKWriterTest {

    private Point[] points = {
            new Point(2, 3, 0.0),
            new Point(5, 3, 0),
            new Point(6, 4, 0),
            new Point(5.5, 5.2, 0),
            new Point(4, 5, 0)
    };
    private Cell[] cells = {
            new Cell(new int[]{0, 1, 4}, VTK_TRIANGLE),
            new Cell(new int[]{1, 2, 3, 4}, VTK_QUAD)
    };
    private ScalarData[] pointScalarData = {
            new ScalarData("Temperature", new double[]{200, 300, 250, 230, 400}),
            new ScalarData("Pressure", new double[]{126920.01, 133028.18, 83980.67, 85415.18, 62601.26})
    };
    private VectorData[] pointVectorData = {
            new VectorData("Velocity", new Vector[]{
                    new Vector(4.12, -0.68, 2.67),
                    new Vector(-2.65, 0.00, 1.14),
                    new Vector(-0.00, 2.09, 0.15),
                    new Vector(-0.73, 0.39, 0.64),
                    new Vector(-0.75, 1.22, -0.33)
            })
    };

    private ScalarData[] cellScalarData = {
            new ScalarData("speed", new double[]{1.5, 20.8})
    };
    private VectorData[] cellVectorData = {
            new VectorData("Vorticity", new Vector[]{
                    new Vector(-6.23, 8.87, -6.28),
                    new Vector(15.79, -14.54, -2.30)
            }),
            new VectorData("Acceleration", new Vector[]{
                    new Vector(0.34, -1.70, 0.72),
                    new Vector(0.33, -1.55, -0.00)
            })
    };

    @Test
    public void writeASCII() {
        UnstructuredGrid gridWithData = new UnstructuredGrid(points, cells, pointScalarData, pointVectorData, cellScalarData, cellVectorData);
        UnstructuredGrid gridWithoutData = new UnstructuredGrid(points, cells, null, null, null, null);
        File outFileWithData = new File("src/test/resources/unstructuredTestXML_withData_ASCII.vtu");
        File outFileWithOutData = new File("src/test/resources/unstructuredTestXML_withoutData_ASCII.vtu");
        try {
            new UnstructuredGridXmlVtKWriter(gridWithData).setFormat(ASCII).write(outFileWithData);
            new UnstructuredGridXmlVtKWriter(gridWithoutData).setFormat(ASCII).write(outFileWithOutData);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Open manually following files in a VTK visualization software, such as ParaView:");
        System.out.println(Arrays.stream(new File[]{outFileWithData, outFileWithOutData})
                .map(File::toString)
                .map(s -> "    " + s)
                .collect(Collectors.joining("\n")));

    }

    @Test
    public void writeBINARY() {
        UnstructuredGrid gridWithData = new UnstructuredGrid(points, cells, pointScalarData, pointVectorData, cellScalarData, cellVectorData);
        UnstructuredGrid gridWithoutData = new UnstructuredGrid(points, cells, null, null, null, null);
        File outFileWithData = new File("src/test/resources/unstructuredTestXML_withData_BINARY.vtu");
        File outFileWithOutData = new File("src/test/resources/unstructuredTestXML_withoutData_BINARY.vtu");
        File outFileWithDataBigEndian = new File("src/test/resources/unstructuredTestXML_withData_BINARY_BigEndian.vtu");
        File outFileWithOutDataBigEndian = new File("src/test/resources/unstructuredTestXML_withoutData_BINARY_BigEndian.vtu");
        File outFileWithDataNotCompressed = new File("src/test/resources/unstructuredTestXML_withData_BINARY_UnCompressed.vtu");
        try {
            UnstructuredGridXmlVtKWriter w1 = new UnstructuredGridXmlVtKWriter(gridWithData).setFormat(BINARY);
            UnstructuredGridXmlVtKWriter w2 = new UnstructuredGridXmlVtKWriter(gridWithoutData).setFormat(BINARY);

            w1.setByteOrder(LITTLE_ENDIAN);
            w1.write(outFileWithData);

            w2.setByteOrder(LITTLE_ENDIAN);
            w2.write(outFileWithOutData);

            w1.setByteOrder(BIG_ENDIAN);
            w1.write(outFileWithDataBigEndian);

            w2.setByteOrder(BIG_ENDIAN);
            w2.write(outFileWithOutDataBigEndian);

            w1.setCompressed(false);
            w1.write(outFileWithDataNotCompressed);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Open manually following files in a VTK visualization software, such as ParaView:");
        System.out.println(Arrays.stream(new File[]{
                outFileWithData, outFileWithOutData, outFileWithDataBigEndian,
                outFileWithOutDataBigEndian, outFileWithDataNotCompressed})
                .map(File::toString)
                .map(s -> "    " + s)
                .collect(Collectors.joining("\n")));
    }
}