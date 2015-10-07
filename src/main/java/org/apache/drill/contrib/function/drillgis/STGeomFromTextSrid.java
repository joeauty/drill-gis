package org.apache.drill.contrib.function.drillgis;

import javax.inject.Inject;

import org.apache.drill.exec.expr.DrillSimpleFunc;
import org.apache.drill.exec.expr.annotations.FunctionTemplate;
import org.apache.drill.exec.expr.annotations.Output;
import org.apache.drill.exec.expr.annotations.Param;
import org.apache.drill.exec.expr.holders.NullableIntHolder;
import org.apache.drill.exec.expr.holders.NullableVarCharHolder;
import org.apache.drill.exec.expr.holders.VarBinaryHolder;

import io.netty.buffer.DrillBuf;

@FunctionTemplate(name = "st_geomfromtext", scope = FunctionTemplate.FunctionScope.SIMPLE, nulls = FunctionTemplate.NullHandling.NULL_IF_NULL)
public class STGeomFromTextSrid implements DrillSimpleFunc {
	@Param
	NullableVarCharHolder input;

	@Param
	NullableIntHolder sridParam;

	@Output
	VarBinaryHolder out;

	@Inject
	DrillBuf buffer;

	public void setup() {
	}

	public void eval() {
		int srid = sridParam.value;
		String wktText = org.apache.drill.exec.expr.fn.impl.StringFunctionHelpers.toStringFromUTF8(input.start,
				input.end, input.buffer);

		com.esri.core.geometry.ogc.OGCGeometry geom;

		geom = com.esri.core.geometry.ogc.OGCGeometry
				.fromText(wktText);
		geom.setSpatialReference(com.esri.core.geometry.SpatialReference.create(srid));
		
		java.nio.ByteBuffer pointBytes = geom.asBinary();
		out.buffer =  buffer;
		out.start = 0;
		out.end = pointBytes.remaining();
		buffer.setBytes(0, pointBytes);
	}
}