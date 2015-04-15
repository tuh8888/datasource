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
package edu.ucdenver.ccp.fileparsers.ncbi.gene;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

import edu.ucdenver.ccp.common.collections.CollectionsUtil;
import edu.ucdenver.ccp.common.file.CharacterEncoding;
import edu.ucdenver.ccp.common.file.FileUtil;
import edu.ucdenver.ccp.datasource.fileparsers.RecordReader;
import edu.ucdenver.ccp.datasource.fileparsers.test.RecordReaderTester;
import edu.ucdenver.ccp.datasource.identifiers.ncbi.gene.EntrezGeneID;
import edu.ucdenver.ccp.datasource.identifiers.ncbi.gene.GiNumberID;
import edu.ucdenver.ccp.datasource.identifiers.ncbi.refseq.RefSeqID;
import edu.ucdenver.ccp.datasource.identifiers.ncbi.taxonomy.NcbiTaxonomyID;

/**
 * 
 * @author Bill Baumgartner
 * 
 */
@Ignore("originally written for the gene2accession file, but this class was revised to parse only the gene2refseq file. Tests seem to pass but that might not be a good thing.")
public class EntrezGene2RefSeqFileParserTest extends RecordReaderTester {

	@Override
	protected String getSampleFileName() {
		return "EntrezGene_gene2accession";
	}

	@Override
	protected RecordReader<?> initSampleRecordReader() throws IOException {
		return new EntrezGene2RefseqFileParser(sampleInputFile, CharacterEncoding.US_ASCII);
	}

	@Test
	public void testParser() {
		try {
			EntrezGene2RefseqFileParser parser = new EntrezGene2RefseqFileParser(sampleInputFile,
					CharacterEncoding.US_ASCII);

			// /* Test calling next() before hasNext() */
			// assertNull(parser.next());

			if (parser.hasNext()) {
				/*
				 * 10090 16822 PROVISIONAL NM_010696.3 118130099 NP_034826.2 31543115 AC_000033.1
				 * 83274085 36458438 36503503 + Alternate assembly (based on Celera)
				 */
				EntrezGene2RefseqFileData record = parser.next();
				assertEquals(new NcbiTaxonomyID(10090), record.getTaxonID());
				assertEquals(new EntrezGeneID(16822), record.getGeneID());
				assertEquals("PROVISIONAL", record.getStatus());
				assertEquals(new RefSeqID("NM_010696.3"), record.getRNA_nucleotide_accession_dot_version());
				assertEquals(new GiNumberID(118130099), record.getRNA_nucleotide_gi());
				assertEquals(new RefSeqID("NP_034826.2"), record.getProtein_accession_dot_version());
				assertEquals(new GiNumberID(31543115), record.getProtein_gi());
				assertEquals(new RefSeqID("AC_000033.1"), record.getGenomic_nucleotide_accession_dot_version());
				assertEquals(new GiNumberID(83274085), record.getGenomic_nucleotide_gi());
				assertEquals((Integer) 36458438, record.getStart_position_on_the_genomic_accession());
				assertEquals((Integer) 36503503, record.getEnd_position_on_the_genomic_accession());
				assertEquals('+', record.getOrientation());
				assertEquals("Alternate assembly (based on Celera)", record.getAssembly());
			} else {
				fail("Parser should have returned a record here.");
			}

			if (parser.hasNext()) {
				/*
				 * 10090 16825 - - - - - AC108484.25 31076542 69411 76442 - -
				 */
				EntrezGene2RefseqFileData record = parser.next();
				assertEquals(new NcbiTaxonomyID(10090), record.getTaxonID());
				assertEquals(new EntrezGeneID(16825), record.getGeneID());
				assertNull(record.getStatus());
				assertNull(record.getRNA_nucleotide_accession_dot_version());
				assertNull(record.getRNA_nucleotide_gi());
				assertNull(record.getProtein_accession_dot_version());
				assertNull(record.getProtein_gi());
				assertNull(record.getGenomic_nucleotide_accession_dot_version());
				assertEquals(new GiNumberID(31076542), record.getGenomic_nucleotide_gi());
				assertEquals((Integer) 69411, record.getStart_position_on_the_genomic_accession());
				assertEquals((Integer) 76442, record.getEnd_position_on_the_genomic_accession());
				assertEquals('-', record.getOrientation());
				assertNull(record.getAssembly());
			} else {
				fail("Parser should have returned a record here.");
			}

			if (parser.hasNext()) {
				/*
				 * 10090 16825 - - - AAC40064.1 2827901 AF024524.1 2827900 - - ? -
				 */
				EntrezGene2RefseqFileData record = parser.next();
				assertEquals(new NcbiTaxonomyID(10090), record.getTaxonID());
				assertEquals(new EntrezGeneID(16825), record.getGeneID());
				assertNull(record.getStatus());
				assertNull(record.getRNA_nucleotide_accession_dot_version());
				assertNull(record.getRNA_nucleotide_gi());
				assertNull(record.getProtein_accession_dot_version());
				assertEquals(new GiNumberID(2827901), record.getProtein_gi());
				assertNull(record.getGenomic_nucleotide_accession_dot_version());
				assertEquals(new GiNumberID(2827900), record.getGenomic_nucleotide_gi());
				assertNull(record.getStart_position_on_the_genomic_accession());
				assertNull(record.getEnd_position_on_the_genomic_accession());
				assertEquals('?', record.getOrientation());
				assertNull(record.getAssembly());
			} else {
				fail("Parser should have returned a record here.");
			}

			assertFalse(parser.hasNext());

		} catch (IOException ioe) {
			ioe.printStackTrace();
			fail("Parser threw an IOException");
		}

	}

	@Test
	public void testGetProteinGiID2EntrezGeneIDMap() throws Exception {
		Map<GiNumberID, Set<EntrezGeneID>> proteinAccession2EntrezGeneIDMap = EntrezGene2RefseqFileParser
				.getProteinGiID2EntrezGeneIDMap(sampleInputFile, CharacterEncoding.US_ASCII, new NcbiTaxonomyID(10090));

		Map<GiNumberID, Set<EntrezGeneID>> expectedProteinAccession2EntrezGeneIDMap = new HashMap<GiNumberID, Set<EntrezGeneID>>();
		Set<EntrezGeneID> entrezGenes1 = new HashSet<EntrezGeneID>();
		entrezGenes1.add(new EntrezGeneID(16822));
		expectedProteinAccession2EntrezGeneIDMap.put(new GiNumberID(31543115), entrezGenes1);
		Set<EntrezGeneID> entrezGenes2 = new HashSet<EntrezGeneID>();
		entrezGenes2.add(new EntrezGeneID(16825));
		expectedProteinAccession2EntrezGeneIDMap.put(new GiNumberID(2827901), entrezGenes2);

		/* Maps should be identical */
		assertEquals(expectedProteinAccession2EntrezGeneIDMap, proteinAccession2EntrezGeneIDMap);
	}

	@Test
	public void testGetProteinAccessionID2EntrezGeneIDMap() throws Exception {
		Map<RefSeqID, Set<EntrezGeneID>> proteinAccession2EntrezGeneIDMap = EntrezGene2RefseqFileParser
				.getProteinAccessionID2EntrezGeneIDMap(sampleInputFile, CharacterEncoding.US_ASCII, new NcbiTaxonomyID(
						10090));

		Map<RefSeqID, Set<EntrezGeneID>> expectedProteinAccession2EntrezGeneIDMap = new HashMap<RefSeqID, Set<EntrezGeneID>>();
		Set<EntrezGeneID> entrezGenes1 = new HashSet<EntrezGeneID>();
		entrezGenes1.add(new EntrezGeneID(16822));
		expectedProteinAccession2EntrezGeneIDMap.put(new RefSeqID("NP_034826"), entrezGenes1);

		/* Maps should be identical */
		assertEquals(expectedProteinAccession2EntrezGeneIDMap, proteinAccession2EntrezGeneIDMap);
	}

	protected Map<File, List<String>> getExpectedOutputFile2LinesMap() {
		List<String> lines = CollectionsUtil
				.createList(
						"<http://www.ncbi.nlm.nih.gov/gene/ENTREZ_GENE2ACCESSION_RECORD_EG_16822> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.ncbi.nlm.nih.gov/gene/EntrezGene2AccessionRecord> .",
						"<http://www.ncbi.nlm.nih.gov/gene/ENTREZ_GENE2ACCESSION_RECORD_EG_16822> <http://www.informatics.jax.org/isLinkedToEntrezGeneICE> <http://www.ncbi.nlm.nih.gov/gene/EG_16822_ICE> .",
						"<http://www.ncbi.nlm.nih.gov/gene/ENTREZ_GENE2ACCESSION_RECORD_EG_16822> <http://www.ncbi.nlm.nih.gov/gene/isLinkedToRnaNucleotideAccession> <http://www.ncbi.nlm.nih.gov/refseq/NM_010696.3_ICE> .",
						"<http://www.ncbi.nlm.nih.gov/gene/ENTREZ_GENE2ACCESSION_RECORD_EG_16822> <http://www.ncbi.nlm.nih.gov/gene/isLinkedToRnaNucleotideGi> <http://www.ncbi.nlm.nih.gov/genbank/GI_118130099_ICE> .",
						"<http://www.ncbi.nlm.nih.gov/gene/ENTREZ_GENE2ACCESSION_RECORD_EG_16822> <http://www.ncbi.nlm.nih.gov/gene/isLinkedToProteinAccession> <http://www.ncbi.nlm.nih.gov/refseq/NP_034826.2_ICE> .",
						"<http://www.ncbi.nlm.nih.gov/gene/ENTREZ_GENE2ACCESSION_RECORD_EG_16822> <http://www.ncbi.nlm.nih.gov/gene/isLinkedToProteinGi> <http://www.ncbi.nlm.nih.gov/genbank/GI_31543115_ICE> .",
						"<http://www.ncbi.nlm.nih.gov/gene/ENTREZ_GENE2ACCESSION_RECORD_EG_16822> <http://www.ncbi.nlm.nih.gov/gene/isLinkedToGenomicNucleotideAccession> <http://www.ncbi.nlm.nih.gov/refseq/AC_000033.1_ICE> .",
						"<http://www.ncbi.nlm.nih.gov/gene/ENTREZ_GENE2ACCESSION_RECORD_EG_16822> <http://www.ncbi.nlm.nih.gov/gene/isLinkedToGenomicNucleotideGi> <http://www.ncbi.nlm.nih.gov/genbank/GI_83274085_ICE> .",
						"<http://www.ncbi.nlm.nih.gov/gene/ENTREZ_GENE2ACCESSION_RECORD_EG_16825> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.ncbi.nlm.nih.gov/gene/EntrezGene2AccessionRecord> .",
						"<http://www.ncbi.nlm.nih.gov/gene/ENTREZ_GENE2ACCESSION_RECORD_EG_16825> <http://www.informatics.jax.org/isLinkedToEntrezGeneICE> <http://www.ncbi.nlm.nih.gov/gene/EG_16825_ICE> .",
						"<http://www.ncbi.nlm.nih.gov/gene/ENTREZ_GENE2ACCESSION_RECORD_EG_16825> <http://www.ncbi.nlm.nih.gov/gene/isLinkedToGenomicNucleotideAccession> <http://www.ncbi.nlm.nih.gov/refseq/AC108484.25_ICE> .",
						"<http://www.ncbi.nlm.nih.gov/gene/ENTREZ_GENE2ACCESSION_RECORD_EG_16825> <http://www.ncbi.nlm.nih.gov/gene/isLinkedToGenomicNucleotideGi> <http://www.ncbi.nlm.nih.gov/genbank/GI_31076542_ICE> .",
						"<http://www.ncbi.nlm.nih.gov/gene/ENTREZ_GENE2ACCESSION_RECORD_EG_16825> <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://www.ncbi.nlm.nih.gov/gene/EntrezGene2AccessionRecord> .",
						"<http://www.ncbi.nlm.nih.gov/gene/ENTREZ_GENE2ACCESSION_RECORD_EG_16825> <http://www.informatics.jax.org/isLinkedToEntrezGeneICE> <http://www.ncbi.nlm.nih.gov/gene/EG_16825_ICE> .",
						"<http://www.ncbi.nlm.nih.gov/gene/ENTREZ_GENE2ACCESSION_RECORD_EG_16825> <http://www.ncbi.nlm.nih.gov/gene/isLinkedToProteinAccession> <http://www.ncbi.nlm.nih.gov/refseq/AAC40064.1_ICE> .",
						"<http://www.ncbi.nlm.nih.gov/gene/ENTREZ_GENE2ACCESSION_RECORD_EG_16825> <http://www.ncbi.nlm.nih.gov/gene/isLinkedToProteinGi> <http://www.ncbi.nlm.nih.gov/genbank/GI_2827901_ICE> .",
						"<http://www.ncbi.nlm.nih.gov/gene/ENTREZ_GENE2ACCESSION_RECORD_EG_16825> <http://www.ncbi.nlm.nih.gov/gene/isLinkedToGenomicNucleotideAccession> <http://www.ncbi.nlm.nih.gov/refseq/AF024524.1_ICE> .",
						"<http://www.ncbi.nlm.nih.gov/gene/ENTREZ_GENE2ACCESSION_RECORD_EG_16825> <http://www.ncbi.nlm.nih.gov/gene/isLinkedToGenomicNucleotideGi> <http://www.ncbi.nlm.nih.gov/genbank/GI_2827900_ICE> .");
		Map<File, List<String>> file2LinesMap = new HashMap<File, List<String>>();
		file2LinesMap.put(FileUtil.appendPathElementsToDirectory(outputDirectory, "entrezgene-2refseqOrAccession.nt"),
				lines);
		return file2LinesMap;
	}

	protected Map<String, Integer> getExpectedFileStatementCounts() {
		Map<String, Integer> counts = new HashMap<String, Integer>();
		counts.put("entrezgene-2refseqOrAccession.nt", 18);
		counts.put("kabob-meta-entrezgene-2refseqOrAccession.nt", 6);
		return counts;
	}

}