/*
 * Copyright (C) 2009 Center for Computational Pharmacology, University of Colorado School of Medicine
 * 
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 * 
 */
package edu.ucdenver.ccp.fileparsers.transfac;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileUtil;
import edu.ucdenver.ccp.datasource.fileparsers.RecordReader;
import edu.ucdenver.ccp.datasource.fileparsers.test.RecordReaderTester;
import edu.ucdenver.ccp.datasource.identifiers.transfac.TransfacFactorID;
import edu.ucdenver.ccp.datasource.identifiers.transfac.TransfacMatrixID;

/**
 * 
 * @author Bill Baumgartner
 * 
 */
public class TransfacMatrixDatFileParserTest extends RecordReaderTester {

	private final static String SAMPLE_TRANSFAC_MATRIX_DAT_FILE_NAME = "Transfac_matrix.dat";

	@Override
	protected String getSampleFileName() {
		return SAMPLE_TRANSFAC_MATRIX_DAT_FILE_NAME;
	}

	@Override
	protected RecordReader<?> initSampleRecordReader() throws IOException {
		return new TransfacMatrixDatFileParser(sampleInputFile, CharacterEncoding.US_ASCII);
	}

	@Test
	public void testParser() {
		TransfacMatrixDatFileParser parser = null;
		try {
			parser = new TransfacMatrixDatFileParser(sampleInputFile, CharacterEncoding.US_ASCII);
		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail("Parser threw an IOException");
		}

		validateAll(parser);
	}

	

	private void validateAll(TransfacMatrixDatFileParser parser) {
		if (parser.hasNext()) {
			validateRecord1(parser.next());
		} else {
			fail("Parser should have returned a record here.");
		}

		if (parser.hasNext()) {
			validateRecord2(parser.next());
		} else {
			fail("Parser should have returned a record here.");
		}

		assertFalse(parser.hasNext());
	}

	private void validateRecord1(TransfacMatrixDatFileData record) {
		validateRecord(record, new TransfacMatrixID("M00001"), "T00526");
	}

	private void validateRecord2(TransfacMatrixDatFileData record) {
		validateRecord(record, new TransfacMatrixID("M00006"), "T00505", "T01005", "T01004");
	}

	private void validateRecord(TransfacMatrixDatFileData record, TransfacMatrixID transfacMatrixID,
			String... bindingFactorIDs) {
		assertEquals(transfacMatrixID, record.getTransfacMatrixID());
		Set<TransfacFactorID> expectedLinkedBindingFactorIDs = new HashSet<TransfacFactorID>();
		for (String bindingFactorID : bindingFactorIDs)
			expectedLinkedBindingFactorIDs.add(new TransfacFactorID(bindingFactorID));
		assertEquals(expectedLinkedBindingFactorIDs, record.getLinkedBindingFactorIDs());

	}

	protected Map<File, List<String>> getExpectedOutputFile2LinesMap() {
		final String NS = "<http://kabob.ucdenver.edu/iao/transfac/";
		List<String> lines = CollectionsUtil
				.createList(
						NS + "M00001_ICE> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://kabob.ucdenver.edu/iao/transfac/TransfacMatrixIce1> .",
						NS + "M00001_ICE> <http://kabob.ucdenver.edu/iao/transfac/hasTransfacGeneID> \"M00001\"@en .",
						NS + "M00001_ICE> <http://purl.obolibrary.org/obo/IAO_0000136> <http://www.gene-regulation.com/transfac/M00001> .",
						NS + "M00001_ICE> <http://kabob.ucdenver.edu/iao/transfac/isLinkedToTransfacBindingFactorICE> <http://kabob.ucdenver.edu/iao/transfac/T00526_ICE> .",
						NS + "M00006_ICE> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://kabob.ucdenver.edu/iao/transfac/TransfacMatrixIce1> .",
						NS + "M00006_ICE> <http://kabob.ucdenver.edu/iao/transfac/hasTransfacGeneID> \"M00006\"@en .",
						NS + "M00006_ICE> <http://purl.obolibrary.org/obo/IAO_0000136> <http://www.gene-regulation.com/transfac/M00006> .",
						NS + "M00006_ICE> <http://kabob.ucdenver.edu/iao/transfac/isLinkedToTransfacBindingFactorICE> <http://kabob.ucdenver.edu/iao/transfac/T00505_ICE> .",
						NS + "M00006_ICE> <http://kabob.ucdenver.edu/iao/transfac/isLinkedToTransfacBindingFactorICE> <http://kabob.ucdenver.edu/iao/transfac/T01004_ICE> .",
						NS + "M00006_ICE> <http://kabob.ucdenver.edu/iao/transfac/isLinkedToTransfacBindingFactorICE> <http://kabob.ucdenver.edu/iao/transfac/T01005_ICE> .");
		Map<File, List<String>> file2LinesMap = new HashMap<File, List<String>>();
		file2LinesMap.put(FileUtil.appendPathElementsToDirectory(outputDirectory, "transfac-matrix.nt"), lines);
		return file2LinesMap;
	}

	protected Map<String, Integer> getExpectedFileStatementCounts() {
		Map<String, Integer> counts = new HashMap<String, Integer>();
		counts.put("transfac-matrix.nt",10);
		counts.put("kabob-meta-transfac-matrix.nt", 6);
		return counts;
	}

}