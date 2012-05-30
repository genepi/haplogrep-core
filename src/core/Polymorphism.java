package core;


import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import exceptions.parse.sample.InvalidBaseException;
import exceptions.parse.sample.InvalidPolymorphismException;


public class Polymorphism implements Comparable<Polymorphism>{
	protected int position;
	protected Mutations mutation;
	
	private static String rCRS ="gatcacaggtctatcaccctattaaccactcacgggagctctccatgcatttggtattttcgtctggggggtatgcacgcgatagcattgcgagacgctggagccggagcaccctatgtcgcagtatctgtctttgattcctgcctcatcctattatttatcgcacctacgttcaatattacaggcgaacatacttactaaagtgtgttaattaattaatgcttgtaggacataataataacaattgaatgtctgcacagccactttccacacagacatcataacaaaaaatttccaccaaaccccccctcccccgcttctggccacagcacttaaacacatctctgccaaaccccaaaaacaaagaaccctaacaccagcctaaccagatttcaaattttatcttttggcggtatgcacttttaacagtcaccccccaactaacacattattttcccctcccactcccatactactaatctcatcaatacaacccccgcccatcctacccagcacacacacaccgctgctaaccccataccccgaaccaaccaaaccccaaagacaccccccacagtttatgtagcttacctcctcaaagcaatacactgaaaatgtttagacgggctcacatcaccccataaacaaataggtttggtcctagcctttctattagctcttagtaagattacacatgcaagcatccccgttccagtgagttcaccctctaaatcaccacgatcaaaaggaacaagcatcaagcacgcagcaatgcagctcaaaacgcttagcctagccacacccccacgggaaacagcagtgattaacctttagcaataaacgaaagtttaactaagctatactaaccccagggttggtcaatttcgtgccagccaccgcggtcacacgattaacccaagtcaatagaagccggcgtaaagagtgttttagatcaccccctccccaataaagctaaaactcacctgagttgtaaaaaactccagttgacacaaaatagactacgaaagtggctttaacatatctgaacacacaatagctaagacccaaactgggattagataccccactatgcttagccctaaacctcaacagttaaatcaacaaaactgctcgccagaacactacgagccacagcttaaaactcaaaggacctggcggtgcttcatatccctctagaggagcctgttctgtaatcgataaaccccgatcaacctcaccacctcttgctcagcctatataccgccatcttcagcaaaccctgatgaaggctacaaagtaagcgcaagtacccacgtaaagacgttaggtcaaggtgtagcccatgaggtggcaagaaatgggctacattttctaccccagaaaactacgatagcccttatgaaacttaagggtcgaaggtggatttagcagtaaactaagagtagagtgcttagttgaacagggccctgaagcgcgtacacaccgcccgtcaccctcctcaagtatacttcaaaggacatttaactaaaacccctacgcatttatatagaggagacaagtcgtaacatggtaagtgtactggaaagtgcacttggacgaaccagagtgtagcttaacacaaagcacccaacttacacttaggagatttcaacttaacttgaccgctctgagctaaacctagccccaaacccactccaccttactaccagacaaccttagccaaaccatttacccaaataaagtataggcgatagaaattgaaacctggcgcaatagatatagtaccgcaagggaaagatgaaaaattataaccaagcataatatagcaaggactaacccctataccttctgcataatgaattaactagaaataactttgcaaggagagccaaagctaagacccccgaaaccagacgagctacctaagaacagctaaaagagcacacccgtctatgtagcaaaatagtgggaagatttataggtagaggcgacaaacctaccgagcctggtgatagctggttgtccaagatagaatcttagttcaactttaaatttgcccacagaaccctctaaatccccttgtaaatttaactgttagtccaaagaggaacagctctttggacactaggaaaaaaccttgtagagagagtaaaaaatttaacacccatagtaggcctaaaagcagccaccaattaagaaagcgttcaagctcaacacccactacctaaaaaatcccaaacatataactgaactcctcacacccaattggaccaatctatcaccctatagaagaactaatgttagtataagtaacatgaaaacattctcctccgcataagcctgcgtcagattaaaacactgaactgacaattaacagcccaatatctacaatcaaccaacaagtcattattaccctcactgtcaacccaacacaggcatgctcataaggaaaggttaaaaaaagtaaaaggaactcggcaaatcttaccccgcctgtttaccaaaaacatcacctctagcatcaccagtattagaggcaccgcctgcccagtgacacatgtttaacggccgcggtaccctaaccgtgcaaaggtagcataatcacttgttccttaaatagggacctgtatgaatggctccacgagggttcagctgtctcttacttttaaccagtgaaattgacctgcccgtgaagaggcgggcataacacagcaagacgagaagaccctatggagctttaatttattaatgcaaacagtacctaacaaacccacaggtcctaaactaccaaacctgcattaaaaatttcggttggggcgacctcggagcagaacccaacctccgagcagtacatgctaagacttcaccagtcaaagcgaactactatactcaattgatccaataacttgaccaacggaacaagttaccctagggataacagcgcaatcctattctagagtccatatcaacaatagggtttacgacctcgatgttggatcaggacatcccgatggtgcagccgctattaaaggttcgtttgttcaacgattaaagtcctacgtgatctgagttcagaccggagtaatccaggtcggtttctatctacnttcaaattcctccctgtacgaaaggacaagagaaataaggcctacttcacaaagcgccttcccccgtaaatgatatcatctcaacttagtattatacccacacccacccaagaacagggtttgttaagatggcagagcccggtaatcgcataaaacttaaaactttacagtcagaggttcaattcctcttcttaacaacatacccatggccaacctcctactcctcattgtacccattctaatcgcaatggcattcctaatgcttaccgaacgaaaaattctaggctatatacaactacgcaaaggccccaacgttgtaggcccctacgggctactacaacccttcgctgacgccataaaactcttcaccaaagagcccctaaaacccgccacatctaccatcaccctctacatcaccgccccgaccttagctctcaccatcgctcttctactatgaacccccctccccatacccaaccccctggtcaacctcaacctaggcctcctatttattctagccacctctagcctagccgtttactcaatcctctgatcagggtgagcatcaaactcaaactacgccctgatcggcgcactgcgagcagtagcccaaacaatctcatatgaagtcaccctagccatcattctactatcaacattactaataagtggctcctttaacctctccacccttatcacaacacaagaacacctctgattactcctgccatcatgacccttggccataatatgatttatctccacactagcagagaccaaccgaacccccttcgaccttgccgaaggggagtccgaactagtctcaggcttcaacatcgaatacgccgcaggccccttcgccctattcttcatagccgaatacacaaacattattataataaacaccctcaccactacaatcttcctaggaacaacatatgacgcactctcccctgaactctacacaacatattttgtcaccaagaccctacttctaacctccctgttcttatgaattcgaacagcatacccccgattccgctacgaccaactcatacacctcctatgaaaaaacttcctaccactcaccctagcattacttatatgatatgtctccatacccattacaatctccagcattccccctcaaacctaagaaatatgtctgataaaagagttactttgatagagtaaataataggagcttaaacccccttatttctaggactatgagaatcgaacccatccctgagaatccaaaattctccgtgccacctatcacaccccatcctaaagtaaggtcagctaaataagctatcgggcccataccccgaaaatgttggttatacccttcccgtactaattaatcccctggcccaacccgtcatctactctaccatctttgcaggcacactcatcacagcgctaagctcgcactgattttttacctgagtaggcctagaaataaacatgctagcttttattccagttctaaccaaaaaaataaaccctcgttccacagaagctgccatcaagtatttcctcacgcaagcaaccgcatccataatccttctaatagctatcctcttcaacaatatactctccggacaatgaaccataaccaatactaccaatcaatactcatcattaataatcataatagctatagcaataaaactaggaatagccccctttcacttctgagtcccagaggttacccaaggcacccctctgacatccggcctgcttcttctcacatgacaaaaactagcccccatctcaatcatataccaaatctctccctcactaaacgtaagccttctcctcactctctcaatcttatccatcatagcaggcagttgaggtggattaaaccaaacccagctacgcaaaatcttagcatactcctcaattacccacataggatgaataatagcagttctaccgtacaaccctaacataaccattcttaatttaactatttatattatcctaactactaccgcattcctactactcaacttaaactccagcaccacgaccctactactatctcgcacctgaaacaagctaacatgactaacacccttaattccatccaccctcctctccctaggaggcctgcccccgctaaccggctttttgcccaaatgggccattatcgaagaattcacaaaaaacaatagcctcatcatccccaccatcatagccaccatcaccctccttaacctctacttctacctacgcctaatctactccacctcaatcacactactccccatatctaacaacgtaaaaataaaatgacagtttgaacatacaaaacccaccccattcctccccacactcatcgcccttaccacgctactcctacctatctccccttttatactaataatcttatagaaatttaggttaaatacagaccaagagccttcaaagccctcagtaagttgcaatacttaatttctgtaacagctaaggactgcaaaaccccactctgcatcaactgaacgcaaatcagccactttaattaagctaagcccttactagaccaatgggacttaaacccacaaacacttagttaacagctaagcaccctaatcaactggcttcaatctacttctcccgccgccgggaaaaaaggcgggagaagccccggcaggtttgaagctgcttcttcgaatttgcaattcaatatgaaaatcacctcggagctggtaaaaagaggcctaacccctgtctttagatttacagtccaatgcttcactcagccattttacctcacccccactgatgttcgccgaccgttgactattctctacaaaccacaaagacattggaacactatacctattattcggcgcatgagctggagtcctaggcacagctctaagcctccttattcgagccgagctgggccagccaggcaaccttctaggtaacgaccacatctacaacgttatcgtcacagcccatgcatttgtaataatcttcttcatagtaatacccatcataatcggaggctttggcaactgactagttcccctaataatcggtgcccccgatatggcgtttccccgcataaacaacataagcttctgactcttacctccctctctcctactcctgctcgcatctgctatagtggaggccggagcaggaacaggttgaacagtctaccctcccttagcagggaactactcccaccctggagcctccgtagacctaaccatcttctccttacacctagcaggtgtctcctctatcttaggggccatcaatttcatcacaacaattatcaatataaaaccccctgccataacccaataccaaacgcccctcttcgtctgatccgtcctaatcacagcagtcctacttctcctatctctcccagtcctagctgctggcatcactatactactaacagaccgcaacctcaacaccaccttcttcgaccccgccggaggaggagaccccattctataccaacacctattctgatttttcggtcaccctgaagtttatattcttatcctaccaggcttcggaataatctcccatattgtaacttactactccggaaaaaaagaaccatttggatacataggtatggtctgagctatgatatcaattggcttcctagggtttatcgtgtgagcacaccatatatttacagtaggaatagacgtagacacacgagcatatttcacctccgctaccataatcatcgctatccccaccggcgtcaaagtatttagctgactcgccacactccacggaagcaatatgaaatgatctgctgcagtgctctgagccctaggattcatctttcttttcaccgtaggtggcctgactggcattgtattagcaaactcatcactagacatcgtactacacgacacgtactacgttgtagcccacttccactatgtcctatcaataggagctgtatttgccatcataggaggcttcattcactgatttcccctattctcaggctacaccctagaccaaacctacgccaaaatccatttcactatcatattcatcggcgtaaatctaactttcttcccacaacactttctcggcctatccggaatgccccgacgttactcggactaccccgatgcatacaccacatgaaacatcctatcatctgtaggctcattcatttctctaacagcagtaatattaataattttcatgatttgagaagccttcgcttcgaagcgaaaagtcctaatagtagaagaaccctccataaacctggagtgactatatggatgccccccaccctaccacacattcgaagaacccgtatacataaaatctagacaaaaaaggaaggaatcgaaccccccaaagctggtttcaagccaaccccatggcctccatgactttttcaaaaaggtattagaaaaaccatttcataactttgtcaaagttaaattataggctaaatcctatatatcttaatggcacatgcagcgcaagtaggtctacaagacgctacttcccctatcatagaagagcttatcacctttcatgatcacgccctcataatcattttccttatctgcttcctagtcctgtatgcccttttcctaacactcacaacaaaactaactaatactaacatctcagacgctcaggaaatagaaaccgtctgaactatcctgcccgccatcatcctagtcctcatcgccctcccatccctacgcatcctttacataacagacgaggtcaacgatccctcccttaccatcaaatcaattggccaccaatggtactgaacctacgagtacaccgactacggcggactaatcttcaactcctacatacttcccccattattcctagaaccaggcgacctgcgactccttgacgttgacaatcgagtagtactcccgattgaagcccccattcgtataataattacatcacaagacgtcttgcactcatgagctgtccccacattaggcttaaaaacagatgcaattcccggacgtctaaaccaaaccactttcaccgctacacgaccgggggtatactacggtcaatgctctgaaatctgtggagcaaaccacagtttcatgcccatcgtcctagaattaattcccctaaaaatctttgaaatagggcccgtatttaccctatagcaccccctctaccccctctagagcccactgtaaagctaacttagcattaaccttttaagttaaagattaagagaaccaacacctctttacagtgaaatgccccaactaaatactaccgtatggcccaccataattacccccatactccttacactattcctcatcacccaactaaaaatattaaacacaaactaccacctacctccctcaccaaagcccataaaaataaaaaattataacaaaccctgagaaccaaaatgaacgaaaatctgttcgcttcattcattgcccccacaatcctaggcctacccgccgcagtactgatcattctatttccccctctattgatccccacctccaaatatctcatcaacaaccgactaatcaccacccaacaatgactaatcaaactaacctcaaaacaaatgataaccatacacaacactaaaggacgaacctgatctcttatactagtatccttaatcatttttattgccacaactaacctcctcggactcctgcctcactcatttacaccaaccacccaactatctataaacctagccatggccatccccttatgagcgggcacagtgattataggctttcgctctaagattaaaaatgccctagcccacttcttaccacaaggcacacctacaccccttatccccatactagttattatcgaaaccatcagcctactcattcaaccaatagccctggccgtacgcctaaccgctaacattactgcaggccacctactcatgcacctaattggaagcgccaccctagcaatatcaaccattaaccttccctctacacttatcatcttcacaattctaattctactgactatcctagaaatcgctgtcgccttaatccaagcctacgttttcacacttctagtaagcctctacctgcacgacaacacataatgacccaccaatcacatgcctatcatatagtaaaacccagcccatgacccctaacaggggccctctcagccctcctaatgacctccggcctagccatgtgatttcacttccactccataacgctcctcatactaggcctactaaccaacacactaaccatataccaatgatggcgcgatgtaacacgagaaagcacataccaaggccaccacacaccacctgtccaaaaaggccttcgatacgggataatcctatttattacctcagaagtttttttcttcgcaggatttttctgagccttttaccactccagcctagcccctaccccccaattaggagggcactggcccccaacaggcatcaccccgctaaatcccctagaagtcccactcctaaacacatccgtattactcgcatcaggagtatcaatcacctgagctcaccatagtctaatagaaaacaaccgaaaccaaataattcaagcactgcttattacaattttactgggtctctattttaccctcctacaagcctcagagtacttcgagtctcccttcaccatttccgacggcatctacggctcaacattttttgtagccacaggcttccacggacttcacgtcattattggctcaactttcctcactatctgcttcatccgccaactaatatttcactttacatccaaacatcactttggcttcgaagccgccgcctgatactggcattttgtagatgtggtttgactatttctgtatgtctccatctattgatgagggtcttactcttttagtataaatagtaccgttaacttccaattaactagttttgacaacattcaaaaaagagtaataaacttcgccttaattttaataatcaacaccctcctagccttactactaataattattacattttgactaccacaactcaacggctacatagaaaaatccaccccttacgagtgcggcttcgaccctatatcccccgcccgcgtccctttctccataaaattcttcttagtagctattaccttcttattatttgatctagaaattgccctccttttacccctaccatgagccctacaaacaactaacctgccactaatagttatgtcatccctcttattaatcatcatcctagccctaagtctggcctatgagtgactacaaaaaggattagactgaaccgaattggtatatagtttaaacaaaacgaatgatttcgactcattaaattatgataatcatatttaccaaatgcccctcatttacataaatattatactagcatttaccatctcacttctaggaatactagtatatcgctcacacctcatatcctccctactatgcctagaaggaataatactatcgctgttcattatagctactctcataaccctcaacacccactccctcttagccaatattgtgcctattgccatactagtctttgccgcctgcgaagcagcggtgggcctagccctactagtctcaatctccaacacatatggcctagactacgtacataacctaaacctactccaatgctaaaactaatcgtcccaacaattatattactaccactgacatgactttccaaaaaacacataatttgaatcaacacaaccacccacagcctaattattagcatcatccctctactattttttaaccaaatcaacaacaacctatttagctgttccccaaccttttcctccgaccccctaacaacccccctcctaatactaactacctgactcctacccctcacaatcatggcaagccaacgccacttatccagtgaaccactatcacgaaaaaaactctacctctctatactaatctccctacaaatctccttaattataacattcacagccacagaactaatcatattttatatcttcttcgaaaccacacttatccccaccttggctatcatcacccgatgaggcaaccagccagaacgcctgaacgcaggcacatacttcctattctacaccctagtaggctcccttcccctactcatcgcactaatttacactcacaacaccctaggctcactaaacattctactactcactctcactgcccaagaactatcaaactcctgagccaacaacttaatatgactagcttacacaatagcttttatagtaaagatacctctttacggactccacttatgactccctaaagcccatgtcgaagcccccatcgctgggtcaatagtacttgccgcagtactcttaaaactaggcggctatggtataatacgcctcacactcattctcaaccccctgacaaaacacatagcctaccccttccttgtactatccctatgaggcataattataacaagctccatctgcctacgacaaacagacctaaaatcgctcattgcatactcttcaatcagccacatagccctcgtagtaacagccattctcatccaaaccccctgaagcttcaccggcgcagtcattctcataatcgcccacgggcttacatcctcattactattctgcctagcaaactcaaactacgaacgcactcacagtcgcatcataatcctctctcaaggacttcaaactctactcccactaatagctttttgatgacttctagcaagcctcgctaacctcgccttaccccccactattaacctactgggagaactctctgtgctagtaaccacgttctcctgatcaaatatcactctcctacttacaggactcaacatactagtcacagccctatactccctctacatatttaccacaacacaatggggctcactcacccaccacattaacaacataaaaccctcattcacacgagaaaacaccctcatgttcatacacctatcccccattctcctcctatccctcaaccccgacatcattaccgggttttcctcttgtaaatatagtttaaccaaaacatcagattgtgaatctgacaacagaggcttacgaccccttatttaccgagaaagctcacaagaactgctaactcatgcccccatgtctaacaacatggctttctcaacttttaaaggataacagctatccattggtcttaggccccaaaaattttggtgcaactccaaataaaagtaataaccatgcacactactataaccaccctaaccctgacttccctaattccccccatccttaccaccctcgttaaccctaacaaaaaaaactcatacccccattatgtaaaatccattgtcgcatccacctttattatcagtctcttccccacaacaatattcatgtgcctagaccaagaagttattatctcgaactgacactgagccacaacccaaacaacccagctctccctaagcttcaaactagactacttctccataatattcatccctgtagcattgttcgttacatggtccatcatagaattctcactgtgatatataaactcagacccaaacattaatcagttcttcaaatatctactcatcttcctaattaccatactaatcttagttaccgctaacaacctattccaactgttcatcggctgagagggcgtaggaattatatccttcttgctcatcagttgatgatacgcccgagcagatgccaacacagcagccattcaagcaatcctatacaaccgtatcggcgatatcggtttcatcctcgccttagcatgatttatcctacactccaactcatgagacccacaacaaatagcccttctaaacgctaatccaagcctcaccccactactaggcctcctcctagcagcagcaggcaaatcagcccaattaggtctccacccctgactcccctcagccatagaaggccccaccccagtctcagccctactccactcaagcactatagttgtagcaggaatcttcttactcatccgcttccaccccctagcagaaaatagcccactaatccaaactctaacactatgcttaggcgctatcaccactctgttcgcagcagtctgcgcccttacacaaaatgacatcaaaaaaatcgtagccttctccacttcaagtcaactaggactcataatagttacaatcggcatcaaccaaccacacctagcattcctgcacatctgtacccacgccttcttcaaagccatactatttatgtgctccgggtccatcatccacaaccttaacaatgaacaagatattcgaaaaataggaggactactcaaaaccatacctctcacttcaacctccctcaccattggcagcctagcattagcaggaatacctttcctcacaggtttctactccaaagaccacatcatcgaaaccgcaaacatatcatacacaaacgcctgagccctatctattactctcatcgctacctccctgacaagcgcctatagcactcgaataattcttctcaccctaacaggtcaacctcgcttccccacccttactaacattaacgaaaataaccccaccctactaaaccccattaaacgcctggcagccggaagcctattcgcaggatttctcattactaacaacatttcccccgcatcccccttccaaacaacaatccccctctacctaaaactcacagccctcgctgtcactttcctaggacttctaacagccctagacctcaactacctaaccaacaaacttaaaataaaatccccactatgcacattttatttctccaacatactcggattctaccctagcatcacacaccgcacaatcccctatctaggccttcttacgagccaaaacctgcccctactcctcctagacctaacctgactagaaaagctattacctaaaacaatttcacagcaccaaatctccacctccatcatcacctcaacccaaaaaggcataattaaactttacttcctctctttcttcttcccactcatcctaaccctactcctaatcacataacctattcccccgagcaatctcaattacaatatatacaccaacaaacaatgttcaaccagtaactactactaatcaacgcccataatcatacaaagcccccgcaccaataggatcctcccgaatcaaccctgacccctctccttcataaattattcagcttcctacactattaaagtttaccacaaccaccaccccatcatactctttcacccacagcaccaatcctacctccatcgctaaccccactaaaacactcaccaagacctcaacccctgacccccatgcctcaggatactcctcaatagccatcgctgtagtatatccaaagacaaccatcattccccctaaataaattaaaaaaactattaaacccatataacctcccccaaaattcagaataataacacacccgaccacaccgctaacaatcaatactaaacccccataaataggagaaggcttagaagaaaaccccacaaaccccattactaaacccacactcaacagaaacaaagcatacatcattattctcgcacggactacaaccacgaccaatgatatgaaaaaccatcgttgtatttcaactacaagaacaccaatgaccccaatacgcaaaactaaccccctaataaaattaattaaccactcattcatcgacctccccaccccatccaacatctccgcatgatgaaacttcggctcactccttggcgcctgcctgatcctccaaatcaccacaggactattcctagccatgcactactcaccagacgcctcaaccgccttttcatcaatcgcccacatcactcgagacgtaaattatggctgaatcatccgctaccttcacgccaatggcgcctcaatattctttatctgcctcttcctacacatcgggcgaggcctatattacggatcatttctctactcagaaacctgaaacatcggcattatcctcctgcttgcaactatagcaacagccttcataggctatgtcctcccgtgaggccaaatatcattctgaggggccacagtaattacaaacttactatccgccatcccatacattgggacagacctagttcaatgaatctgaggaggctactcagtagacagtcccaccctcacacgattctttacctttcacttcatcttgcccttcattattgcagccctagcaacactccacctcctattcttgcacgaaacgggatcaaacaaccccctaggaatcacctcccattccgataaaatcaccttccacccttactacacaatcaaagacgccctcggcttacttctcttccttctctccttaatgacattaacactattctcaccagacctcctaggcgacccagacaattataccctagccaaccccttaaacacccctccccacatcaagcccgaatgatatttcctattcgcctacacaattctccgatccgtccctaacaaactaggaggcgtccttgccctattactatccatcctcatcctagcaataatccccatcctccatatatccaaacaacaaagcataatatttcgcccactaagccaatcactttattgactcctagccgcagacctcctcattctaacctgaatcggaggacaaccagtaagctacccttttaccatcattggacaagtagcatccgtactatacttcacaacaatcctaatcctaataccaactatctccctaattgaaaacaaaatactcaaatgggcctgtccttgtagtataaactaatacaccagtcttgtaaaccggagatgaaaacctttttccaaggacaaatcagagaaaaagtctttaactccaccattagcacccaaagctaagattctaatttaaactattctctgttctttcatggggaagcagatttgggtaccacccaagtattgactcacccatcaacaaccgctatgtatttcgtacattactgccagccaccatgaatattgtacggtaccataaatacttgaccacctgtagtacataaaaacccaatccacatcaaaaccccctccccatgcttacaagcaagtacagcaatcaaccctcaactatcacacatcaactgcaactccaaagccacccctcacccactaggataccaacaaacctacccacccttaacagtacatagtacataaagccatttaccgtacatagcacattacagtcaaatcccttctcgtccccatggatgacccccctcagataggggtcccttgaccaccatcctccgtgaaatcaatatcccgcacaagagtgctactctcctcgctccgggcccataacacttgggggtagctaaagtgaactgtatccgacatctggttcctacttcagggtcataaagcctaaatagcccacacgttccccttaaataagacatcacgatg";
	
	boolean isBackMutation = false;
	String numberOfIns = "";
	String insertedPolys = "";
	
	
	

	public Polymorphism(int newPosition,Mutations mutatedBase)
	{
		this.mutation = mutatedBase;
		this.position = newPosition;
	}
	
	public Polymorphism(String phyloString) throws NumberFormatException, InvalidPolymorphismException {
		parse(phyloString);
	}
	
	public Polymorphism(Polymorphism currentPoly) {
		this.position= currentPoly.position;
		this.mutation= currentPoly.mutation;
		this.isBackMutation= currentPoly.isBackMutation;
		this.numberOfIns = currentPoly.numberOfIns;
		this.insertedPolys = currentPoly.insertedPolys;
	}

//	public static void changePhyloGeneticWeight(Polymorphism poly, String phylotreeString, double newPhylogeneticWeight)
//	{		
//		phyloGeneticWeights.put(phylotreeString+poly.toString(), newPhylogeneticWeight);
//
//	}
//	
//	public double getMutationRate(String phylotreeString)
//	{
//		if(phyloGeneticWeights.containsKey(phylotreeString+this.toString()))
//			return phyloGeneticWeights.get(phylotreeString+this.toString());
//		
//		else
//			return 0;
//
//	}

	public boolean isMTHotspot()
	{
		try {
			if(this.equals(new Polymorphism("315.1C")))
				return true;		
			if(this.equals(new Polymorphism("309.1C")))
				return true;
			if(this.equals(new Polymorphism("309.1CC")))
				return true;		
			if(this.equals(new Polymorphism("523d")))
				return true;
			if(this.equals(new Polymorphism("524d")))
				return true;		
			if(this.equals(new Polymorphism("16182C")))
				return true;
			if(this.equals(new Polymorphism("16183C")))
				return true;		
			if(this.equals(new Polymorphism("16193.1C")))
				return true;
			if(this.equals(new Polymorphism("16193.1CC")))
				return true;		
			if(this.equals(new Polymorphism("16519")))
				return true;
			else
				return false;
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidPolymorphismException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}
	public int getPosition() {
		return position;
	}

	public boolean isBackMutation() {
		return isBackMutation;
	}

	public void setBackMutation(boolean isBackMutation) {
		this.isBackMutation = isBackMutation;
	}

	public boolean equals(Object p)
	{
		if(!(p instanceof Polymorphism))
			return false;
		
		if(this.position == ((Polymorphism)p).position 
				&& this.mutation == ((Polymorphism)p).mutation){
			
			//insertions
			if(((Polymorphism)p).getMutation().equals(Mutations.INS)){
				
				if(((Polymorphism)p).insertedPolys.contains(this.insertedPolys))
					return true;
				else return false;
			} 
			//end insertions
			
			else if(((Polymorphism)p).isBackMutation != this.isBackMutation)
				return false;
			
			else return true;
		}
		
		else
			return false;
	}	

	public String toString()
	{
		if(!isBackMutation)
		{
			if(this.mutation == Mutations.INS)
				return position  + numberOfIns + insertedPolys;
			
			else if(this.mutation == Mutations.DEL)
				return position +"d";
				
			else
			return position + mutation.toString().trim();
		}
		
		else
			return position + mutation.toString().trim() + "!";
	}
	
	//Remove mutations if transitions ...
	public String toStringShortVersion()
	{
		if(!isBackMutation)
		{
			if(this.mutation == Mutations.INS)
				return position  + numberOfIns + insertedPolys;
			
			else if(this.mutation == Mutations.DEL)
				return position +"d";
				
			else{
				/*Polymorphism p = new Polymorphism(this);
				try {
					p.getTransitionPoly(position);
				} catch (InvalidBaseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}*/
				
				try {
					if(isTransitionPoly())//this.equals(p))
						return String.valueOf(position);
					
					else
						return position + mutation.toString().trim();
				} catch (InvalidBaseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return null;
				}
			}
		}
		
		else
			return position + mutation.toString().trim() + "!";
	}
	
	public static String convertToATBackmutation(String poly){
		if(poly.contains("!")){
			poly = poly.replace("!", "");
			return "@" + poly;
		}
		else
			return poly;
	}
	public static Polymorphism getReferenceBase(int position) throws InvalidBaseException 
	{
		String base = String.valueOf(rCRS.charAt(position - 1));
		base = base.toUpperCase();
		
		
		return new Polymorphism(position,Mutations.getBase(base));	
	}
	
	public static String getReferenceBaseSingle(int position) throws InvalidBaseException 
	{
		String base = String.valueOf(rCRS.charAt(position - 1));
		base = base.toUpperCase();
		return base;	
	}
	
	
	
	public  Polymorphism getReferenceBase()
	{
		try {
			return  getReferenceBase(this.position);
		} catch (InvalidBaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	private void parse(String phyloString) throws InvalidPolymorphismException
	{
		// Because of deletion and insertion more than one resulting polymorphisms are possible
		StringTokenizer st1 = null;
		phyloString=phyloString.trim();
		
		//Only use part in parentheses
		if(phyloString.startsWith("(")) 
			phyloString= phyloString.substring(1,phyloString.length()-1);
		
		// ignore crazy things (TODO need to be checked later)	
		//if((!phyloString.startsWith("(")&&phyloString.contains("("))|| phyloString.contains("R") || phyloString.contains("S") || phyloString.contains("K") || phyloString.contains("Y") || phyloString.contains("W")|| phyloString.contains("M")){
		//	throw new InvalidFormatException(phyloString);
		//}
		// BACKMUTATION
		 if(phyloString.contains("!"))
		{
			phyloString = phyloString.replace("!", "");
			this.setBackMutation(true);			
		}
		
		// DELETIONS
		 if (phyloString.contains("d")||phyloString.contains("D")) {
				phyloString = phyloString.replace("del", "");
				phyloString = phyloString.replace("d", "");
				phyloString = phyloString.replace("DEL", "");
				this.position = Integer.valueOf(phyloString);
				this.mutation = Mutations.DEL;
		}
		
		// always .1 -> INS are changed from .1C,.2C,.3C to .1CCC in Sample Class
		 //.2 -> only from phylotree (455.2T and 2232.2A)
		else if (phyloString.contains(".") ) {
			st1 = new StringTokenizer(phyloString, ".");
			String token = st1.nextToken();
			String token1 = st1.nextToken().trim();
			Pattern p = Pattern.compile("\\d+");
			 Matcher m = p.matcher(token1);
			 m.find();
			 this.position = Integer.valueOf(token);
			 this.mutation = Mutations.INS;
			 String mutationString = "";
			 //i=2 because of PhyloTree
			 for(int i=0; i<=2;i++){
				 String number=String.valueOf(i);
				
				 if(token1.contains(number)){
					 mutationString = token1.replace(number, "");
					 this.numberOfIns="."+number;
					 } 
			 }
			
			 //Check for valid acid
			 int i = 0;
			 try {
				for(i = 0; i < mutationString.length();i++){
					 Mutations.getBase(String.valueOf(mutationString.charAt(i)));}
			} catch (InvalidBaseException e) {
				throw new InvalidPolymorphismException(phyloString,String.valueOf(mutationString.charAt(i)));
			}
			 this.insertedPolys =  mutationString;
		}
		
		// TRANSVERSION If base is included, its a transversion, so just
		// take it as it is.
		else{
		 Pattern p = Pattern.compile("[a-zA-Z]");
		 Matcher m = p.matcher(phyloString);	 
		 if(m.find())
		 {	 
			 
			 try {
				this.mutation = Mutations.getBase(phyloString.substring(m.start(), m.end()));
			} catch (InvalidBaseException e) {
				throw new InvalidPolymorphismException(phyloString,phyloString.substring(m.start(), m.end()));
			}
			 this.position = Integer.valueOf(phyloString.replaceFirst("[a-zA-Z]", ""));
		 }
		 
		 else {

			 p = Pattern.compile("\\d+");
			 m = p.matcher(phyloString);
			 m.find();
			 
			 try {
				Integer.parseInt(phyloString.substring(m.start(), m.end()),10);
			} catch (NumberFormatException e) {
				throw new InvalidPolymorphismException(phyloString);
			}
			 
			 //if(m.end() < phyloString.length())
			///	 throw new InvalidBaseException(phyloString, String.valueOf(phyloString.charAt(m.end())));
			 
			int position = Integer.valueOf(phyloString);
			try {
				getTransitionPoly(position);
			} catch (InvalidBaseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
	
		}	
		}
		
		/*else if (phyloString.contains("C"))
		{
			this.position = Integer.valueOf(phyloString.replace("C", ""));
			this.mutation = Mutations.C;
		}
		else if (phyloString.contains("A")){
			this.position = Integer.valueOf(phyloString.replace("A", ""));
			this.mutation = Mutations.A;			
		}
		else if (phyloString.contains("G")){
			this.position = Integer.valueOf(phyloString.replace("G", ""));
			this.mutation = Mutations.G;
		}
		else if (phyloString.contains("T")){
			this.position = Integer.valueOf(phyloString.replace("T", ""));
			this.mutation = Mutations.T;
		}	*/
		
		// TRANSITION: no base included, so its a Transition.
		
	}
	
	
	private void  getTransitionPoly(int position) throws InvalidBaseException {
		
		this.position = position;
		
		if(getReferenceBase(position).mutation == Mutations.C){
			this.mutation = Mutations.T;
		}
		if(getReferenceBase(position).mutation == Mutations.T){
			this.mutation = Mutations.C;
		}
		if(getReferenceBase(position).mutation == Mutations.G){
			this.mutation = Mutations.A;		
		}
		if(getReferenceBase(position).mutation == Mutations.A){
			this.mutation = Mutations.G;
		}
	}
	
private boolean  isTransitionPoly() throws InvalidBaseException {
				
		if(this.mutation == Mutations.T && getReferenceBase(position).mutation == Mutations.C){
			return true;
		}
		
		if(this.mutation == Mutations.C && getReferenceBase(position).mutation == Mutations.T){
			return true;
		}
		
		if(this.mutation == Mutations.A && getReferenceBase(position).mutation == Mutations.G){
			return true;
		}
		
		if(this.mutation == Mutations.G && getReferenceBase(position).mutation == Mutations.A){
			return true;
		}
		
		else
			return false;
	}


	public Mutations getMutation()
	{
		return mutation;
	}

	public void setMutation(Mutations mutation)
	{
		this.mutation = mutation;
	}

	
	public String getInsertedPolys() {
		return insertedPolys;
	}

	@Override
	public int compareTo(Polymorphism o) {
		
		if(this.position < o.position)
			return -1;
		
		if(this.position == o.position){
			if((this.toString().length() < o.toString().length()))
				return -1;
		    if ((this.toString().length() == o.toString().length()))
		    	return this.mutation.name().compareTo(o.mutation.name());
			else
				return 1; 
		}
		
		else
			return 1;
		/*if(this.toString().compareTo(anotherString) > o.toString())
			return -1;
		if(this.toString() < o.toString())
			return 1;	
		else
			return 0;
		return 0;*/
	}

	@Override
    public int hashCode() {
        return toString().hashCode();
    }

	
	
	
}

