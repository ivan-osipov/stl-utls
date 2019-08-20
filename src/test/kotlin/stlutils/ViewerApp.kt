package stlutils

import javafx.application.Application
import javafx.geometry.Orientation
import javafx.scene.Node
import javafx.scene.Scene
import javafx.scene.layout.FlowPane
import javafx.scene.paint.Color
import javafx.scene.paint.PhongMaterial
import javafx.scene.shape.DrawMode
import javafx.scene.shape.MeshView
import javafx.scene.shape.TriangleMesh
import javafx.stage.Stage
import stlutils.common.SimpleVector3d
import stlutils.common.Triangle
import stlutils.optimization.stlReducer.StlReducer
import stlutils.parser.NormalPolicy
import stlutils.parser.StlParsingManager


class ViewerApp : Application() {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            launch(ViewerApp::class.java, *args)
        }
    }

    override fun start(primaryStage: Stage) {
        primaryStage.title = "3D"
        val root = FlowPane(Orientation.HORIZONTAL)
        val triangles = read3d()

        root.children.add(create3dView(triangles))
        root.children.add(create3dView(reduceTriangles(triangles)))
        primaryStage.scene = Scene(root, 2048.0, 1400.0)
        primaryStage.show()
    }

    private fun reduceTriangles(triangles: List<Triangle>): List<Triangle> {
        return StlReducer().reduce(triangles)
    }

    private fun create3dView(triangles: List<Triangle>): Node {
        val mesh = TriangleMesh()
        mesh.texCoords.addAll(0f, 0f)
        val points = triangles.flatMap {
            listOf(
                SimpleVector3d(it.a.x, it.a.y, it.a.z),
                SimpleVector3d(it.b.x, it.b.y, it.b.z),
                SimpleVector3d(it.c.x, it.c.y, it.c.z)
            )
        }
        for (point in points) {
            mesh.points.addAll(point.x, point.y, point.z)
        }

        for (i in 0 until triangles.size) {
            mesh.faces.addAll(
                i * 3, 0, i * 3 + 1, 0, i * 3 + 2, 0
            )
        }

        val h = 150f                    // Height
        val s = 300f                    // Side
//        mesh.getPoints().addAll(
//            0f, 0f, 0f, // Point 0 - Top
//            0f, h, -s / 2, // Point 1 - Front
//            -s / 2, h, 0f, // Point 2 - Left
//            s / 2, h, 0f, // Point 3 - Back
//            0f, h, s / 2           // Point 4 - Right
//        )
//
//        mesh.getFaces().addAll(
//            0,0,  2,0,  1,0,          // Front left face
//            0,0,  1,0,  3,0,          // Front right face
//            0,0,  3,0,  4,0,          // Back right face
//            0,0,  4,0,  2,0,          // Back left face
//            4,0,  1,0,  2,0,          // Bottom rear face
//            4,0,  3,0,  1,0           // Bottom front face
//        );


        val view = MeshView(mesh)
        view.drawMode = DrawMode.FILL
        view.material = PhongMaterial(Color.AQUAMARINE)
//        view.translateX = 200.0;
//        view.translateY = 100.0;
//        view.translateZ = 200.0;
        return view
    }

    private fun read3d(): List<Triangle> {
        val bytes = ViewerApp::class.java.getResourceAsStream("NODE.stl").readBytes()
        val parsingResults = StlParsingManager(NormalPolicy.COMPUTED).parse(bytes)
        return parsingResults
    }

}