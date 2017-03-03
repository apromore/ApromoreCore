/*
 * Copyright  2009-2017 The Apromore Initiative.
 *
 * This file is part of "Apromore".
 *
 * "Apromore" is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 3 of the
 * License, or (at your option) any later version.
 *
 * "Apromore" is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this program.
 * If not, see <http://www.gnu.org/licenses/lgpl-3.0.html>.
 */
package org.apromore.prodrift.im;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlockStructure implements Comparable<BlockStructure>{

	private String blockName;
	private List<BlockStructure> children = new ArrayList<>();
	private List<BlockStructure> leaves = new ArrayList<>();
	private List<BlockStructure> subBlockStructures = new ArrayList<>();
	private BlockStructure parent = null;
	private String contentString;
	private boolean containsBlockStructure = false;
	private boolean shouldBeUnfoldedInParent = false;
	private boolean areAllBSsSetTobeUnfolded = false;
	private int freq = 0;
	private int size = 0; // num of leaves
	private boolean isSubBS = false;
	private boolean isTau = false;
	private int preOrderInd = -1;
	
	
	public boolean containsBlockStructureName(String blockStructureName)
	{
		
		for (Object node: children) {
			if(node instanceof BlockStructure)
			{
				
				if(((BlockStructure) node).getBlockName().compareToIgnoreCase(blockStructureName) == 0)
					return true;
				
			}
		}
		
		return false;
		
	}
	
	public void setToUnfoldAllBlockStructures()
	{
		
		if(areAllBSsSetTobeUnfolded)
			return;
		
		areAllBSsSetTobeUnfolded = true;
		boolean flag = false;
		
		for (Object node: children) {
			if(node instanceof BlockStructure)
			{
				
				((BlockStructure) node).setShouldBeUnfoldedInParent(true);
				
			}
		}
		
	}
	
	public boolean setToUnfoldBlockStructuresWithName(String blockStructureName)
	{
		
		boolean flag = false;
		
		for (Object node: children) {
			if(node instanceof BlockStructure)
			{
				
				if(((BlockStructure) node).getBlockName().compareToIgnoreCase(blockStructureName) == 0)
				{
					
					((BlockStructure) node).setShouldBeUnfoldedInParent(true);
					flag = true;
					break;
					
				}
					
				
			}
		}
		
		return flag;
		
	}
	
	public void unfoldBlockStructures()
	{
		
		List<String> childrenNames = new ArrayList<>();
		
		StringBuilder blockContent = new StringBuilder();
		blockContent.append(blockName + "(");
		
		for (Object node: children) {
			if(node instanceof BlockStructure)
			{
				
				BlockStructure bs = (BlockStructure) node;
				if(bs.ShouldBeUnfoldedInParent())
				{
					
					StringBuilder childBlockContent = new StringBuilder();
					childBlockContent.append(bs.getBlockName() + "(");
					
					for (int i = 0; i < bs.getLeaves().size(); i++) 
					{
						
						BlockStructure leaf = (BlockStructure)bs.getLeaves().get(i);
						if(i < (bs.getLeaves().size() - 1))
							childBlockContent.append(leaf.getBlockName() + ", ");
						else
							childBlockContent.append(leaf.getBlockName());
						
					}
					childBlockContent.append(")");
					
					childrenNames.add(childBlockContent.toString());
					
				}else
				{
					
					childrenNames.add(bs.getBlockName());
					
				}
				
			}else
			{
				
				childrenNames.add((String)(node));
				
			}
		}
		
		if (blockName.compareToIgnoreCase("XOR_block") == 0 ||
				blockName.compareToIgnoreCase("AND_block") == 0 ||
				blockName.compareToIgnoreCase("Or_block") == 0)
			Collections.sort(childrenNames);
		
		for (int k = 0; k < childrenNames.size(); k++) 
		{
			
			String childName = childrenNames.get(k);
			if(k < (childrenNames.size() - 1))
				blockContent.append(childName + ", ");
			else
				blockContent.append(childName);
			
		}
		
		blockContent.append(")");
		
		contentString = blockContent.toString();
		
	}
	
//	public void cleanContentString()
//	{
//		
//		int index1 = contentString.indexOf("(") + 1;
//		int index2 = contentString.lastIndexOf(")");
//		String childrenString = contentString.substring(index1, index2);
//		childrenString = childrenString.replace("(", "");
//		childrenString = childrenString.replace("), ", "");
//		contentString = blockName + "(" + childrenString + ")";
//		
//	}
	
	public boolean isItCommutative()
	{
		if(getBlockName().compareToIgnoreCase("xor") == 0 ||
				getBlockName().compareToIgnoreCase("and") == 0 ||
				getBlockName().compareToIgnoreCase("or") == 0)
			return true;
		
		return false;
			
	}
	
	public String getBlockName() {
		return blockName;
	}
	public void setBlockName(String blockName) {
		this.blockName = blockName;
	}
	public List<BlockStructure> getChildren() {
		return children;
	}
	public void setNodes(List<BlockStructure> children) {
		this.children = children;
	}
	public List<BlockStructure> getLeaves() {
		return leaves;
	}

	public void setLeaves(List<BlockStructure> leaves) {
		this.leaves = leaves;
	}

	public String getContentString() {
		return contentString;
	}
	public void setContentString(String contentString) {
		this.contentString = contentString;
	}
	public boolean ContainsBlockStructure() {
		return containsBlockStructure;
	}
	public void setContainsBlockStructure(boolean containsBlockStructure) {
		this.containsBlockStructure = containsBlockStructure;
	}
	public boolean ShouldBeUnfoldedInParent() {
		return shouldBeUnfoldedInParent;
	}
	public void setShouldBeUnfoldedInParent(boolean shouldBeUnfoldedInParent) {
		this.shouldBeUnfoldedInParent = shouldBeUnfoldedInParent;
	}

	public int getFreq() {
		return freq;
	}

	public void setFreq(int freq) {
		this.freq = freq;
	}

	public boolean AreAllBSsSetTobeUnfolded() {
		return areAllBSsSetTobeUnfolded;
	}

	public void setAllBSsSetTobeUnfolded(
			boolean areAllBSsSetTobeUnfolded) {
		this.areAllBSsSetTobeUnfolded = areAllBSsSetTobeUnfolded;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public List<BlockStructure> getSubBlockStructures() {
		return subBlockStructures;
	}

	public void setSubBlockStructures(List<BlockStructure> subBlockStructures) {
		this.subBlockStructures = subBlockStructures;
	}
	
	

	public BlockStructure getParent() {
		return parent;
	}

	public void setParent(BlockStructure parent) {
		this.parent = parent;
	}
	
	

	public boolean isSubBS() {
		return isSubBS;
	}

	public void setSubBS(boolean isSubBS) {
		this.isSubBS = isSubBS;
	}

	
	public boolean isTau() {
		return isTau;
	}

	public void setTau(boolean isTau) {
		this.isTau = isTau;
	}
	
	public int getPreOrderInd() {
		return preOrderInd;
	}

	public void setPreOrderInd(int preOrderInd) {
		this.preOrderInd = preOrderInd;
	} 

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((contentString == null) ? 0 : contentString.hashCode());
		return result;
	}
	
	

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BlockStructure other = (BlockStructure) obj;
		if (contentString == null) {
			if (other.contentString != null)
				return false;
		} else if (!contentString.equals(other.contentString))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return contentString;
	}
	
	

	@Override
	public int compareTo(BlockStructure o) {
		return (this.getContentString().compareTo(o.getContentString())) ;
	}



	
	
	
	
}
