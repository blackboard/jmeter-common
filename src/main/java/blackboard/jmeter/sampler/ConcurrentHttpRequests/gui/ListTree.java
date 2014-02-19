/*
 * Copyright (c) 1995, 2008, Oracle and/or its affiliates. All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the
 * following conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice, this list of conditions and the following
 * disclaimer.
 * 
 * - Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided with the distribution.
 * 
 * - Neither the name of Oracle or the names of its contributors may be used to endorse or promote products derived from
 * this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES,
 * INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package blackboard.jmeter.sampler.ConcurrentHttpRequests.gui;

import java.awt.GridLayout;
import java.awt.Toolkit;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
/**
 * 
 * @author zyang
 *
 */
public class ListTree extends JPanel
{
  private static final long serialVersionUID = 2705391645080201296L;
  protected DefaultMutableTreeNode _rootNode;
  protected DefaultTreeModel _treeModel;
  protected JTree _tree;
  private Toolkit _toolkit = Toolkit.getDefaultToolkit();
  String _selectedNodeName;
  ListDetailCardsMap _map;

  public ListTree( ListContentSplitPanel splitPanel )
  {
    super( new GridLayout( 1, 0 ) );

    _rootNode = new DefaultMutableTreeNode( "Root Node" );
    _treeModel = new DefaultTreeModel( _rootNode );
    _tree = new JTree( _treeModel );
    _tree.setEditable( false );
    _tree.getSelectionModel().setSelectionMode( TreeSelectionModel.SINGLE_TREE_SELECTION );
    _tree.setShowsRootHandles( true );
    _tree.addTreeSelectionListener( splitPanel );
    _tree.setRootVisible( false );

    JScrollPane scrollPane = new JScrollPane( _tree );
    add( scrollPane );
  }

  /** Remove the currently selected node. */
  public void removeCurrentNode()
  {

    TreePath currentSelection = _tree.getSelectionPath();
    if ( currentSelection != null )
    {
      DefaultMutableTreeNode currentNode = (DefaultMutableTreeNode) ( currentSelection.getLastPathComponent() );

      MutableTreeNode parent = (MutableTreeNode) ( currentNode.getParent() );
      if ( parent != null )
      {
        int index = parent.getIndex( currentNode );

        _tree.setSelectionRow( index > 0 ? ( index - 1 ) : 1 );
        _treeModel.removeNodeFromParent( currentNode );
        return;
      }
    }

    // Either there was no selection, or the root was selected.
    _toolkit.beep();
  }

  public int getNodeCount()
  {
    return _tree.getRowCount();
  }

  public String getCurrentNodeName()
  {
    String currentNodeName = null;
    DefaultMutableTreeNode currentNode = getCurrentNode();
    if(currentNode!= null)
    {
      currentNodeName = currentNode.toString();
    }
    return currentNodeName;
  }

  public DefaultMutableTreeNode getCurrentNode()
  {
    DefaultMutableTreeNode currentNode = null;
    TreePath currentSelection = _tree.getSelectionPath();
    if ( currentSelection != null )
    {
       currentNode = (DefaultMutableTreeNode) ( currentSelection.getLastPathComponent() );
    }
    return currentNode;
  }
  /** Add child to the currently selected node. */
  public void addObject( Object child )
  {
    addObject( child, true );
  }

  public DefaultMutableTreeNode addObject( Object child, boolean shouldBeVisible )
  {

    DefaultMutableTreeNode childNode = new DefaultMutableTreeNode( child );

    //It is key to invoke this on the TreeModel, and NOT DefaultMutableTreeNode
    _treeModel.insertNodeInto( childNode, _rootNode, _rootNode.getChildCount() );
    //Make sure the user can see the lovely new node.
    if ( shouldBeVisible )
    {
      TreePath treePath = new TreePath( childNode.getPath() );
      _tree.scrollPathToVisible( treePath );
      _tree.setSelectionPath( treePath );
    }

    return childNode;
  }


  public JTree getTree()
  {
    return _tree;
  }

  public void clear()
  {
    DefaultMutableTreeNode root = (DefaultMutableTreeNode) _treeModel.getRoot();
    int count = root.getChildCount();
    for ( int i = 0; i < count; i++ )
    {
      // always remove the ChildAt(0) which is the 1st child node. 
      // You can get ChildAt(2) after 1 out of 3 children node gets removed
      _treeModel.removeNodeFromParent( (DefaultMutableTreeNode) root.getChildAt( 0 ) );
    }
  }

  public void nodeChanged( DefaultMutableTreeNode lastNode )
  {
    _treeModel.nodeChanged( lastNode );
  }
}
